package com.smartmatic.graph.service;

import com.google.gson.Gson;
import com.smartmatic.graph.PostSaveEventListener;
import com.smartmatic.graph.domain.NodeData;
import com.smartmatic.graph.spec.ITransmission;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.UniqueFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.io.File;
import java.util.Arrays;
import java.util.Map;

@Singleton
public class TransmissionBean implements ITransmission {

    private GraphDatabaseService graphDb;

    private static final RelationshipType SUB_DIR = RelationshipType.withName("parent");

    private UniqueFactory.UniqueNodeFactory uniqueFactory;

    private Index<Node> index;

    private String[][] matrix;

    private int counter = 0;

    private PostSaveEventListener<Void> eventListener;

    @PostConstruct
    public void init() {

        graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File("/home/danielbastidas/graphdb"))
                .setConfig(GraphDatabaseSettings.allow_store_upgrade, "false")
                .setConfig(GraphDatabaseSettings.cypher_compiler_tracing, "true")
                .setConfig(GraphDatabaseSettings.cypher_hints_error, "true")
                .setConfig(GraphDatabaseSettings.cypher_parser_version, "3.0").newGraphDatabase();

        matrix = new String[][] { { "Country:Reg", "State1:Reg", "Municipality1:Reg", "Parish1:Reg", "PollingPlace1:Reg", "Tally1:tally:15:55" },
                { "Country:Reg", "State1:Reg", "Municipality1:Reg", "Parish1:Reg", "PollingPlace1:Reg", "Tally2:tally:37:16" },
                { "Country:Reg", "State1:Reg", "Municipality2:Reg", "Tally3:tally:14:42" },
                { "Country:Reg", "State1:Reg", "Municipality2:Reg", "Tally4:tally:63:22" } };

        eventListener = new PostSaveEventListener<Void>();

    }

    @Override
    public String transmission() {

        String tally = null;

        try (Transaction tx = graphDb.beginTx()) {

            tally = Arrays.deepToString(matrix[counter]);

            index = graphDb.index().forNodes("Dir");

            graphDb.registerTransactionEventHandler(eventListener);

            uniqueFactory = new UniqueFactory.UniqueNodeFactory(graphDb, "dirs") {
                @Override
                protected void initialize(Node created, Map<String, Object> properties) {
                    created.addLabel(Label.label("Dir"));
                    created.setProperty("name", properties.get("name"));
                }

                @Override
                protected Node create(Map<String, Object> properties) {
                    System.out.println("Creating node:" + properties.toString());
                    return super.create(properties);
                }
            };

            storePath(matrix[counter]);

            tx.success();
            counter++;

        }

        return tally;
    }

    public void storePath(String[] path) {
        Node dir = uniqueFactory.getOrCreate("name", "Country");// the root node
        String type = null;
        String name = null;
        int candidate1 = 0;
        int candidate2 = 0;
        String array[] = null;
        for (String str : path) {
            array = str.split(":");
            name = array[0];
            type = array[1];
            if (array.length > 2) {
                candidate1 = Integer.parseInt(array[2]);
            }
            if (array.length > 2) {
                candidate2 = Integer.parseInt(array[3]);
            }
            dir = obtainSubDir(dir, name, type, candidate1, candidate2);
        }
    }

    private Node obtainSubDir(Node dir, String name, String type, int candidate1, int candidate2) {
        Node subDir = getSubDir(dir, name);
        if (subDir != null)
            return subDir;
        return createSubDir(dir, name, type, candidate1, candidate2);
    }

    private Node getSubDir(Node dir, String name) {
        for (Relationship rel : dir.getRelationships(SUB_DIR, Direction.OUTGOING)) {
            final Node subDir = rel.getEndNode();
            if (subDir.getProperty("name", "").equals(name))
                return subDir;
        }
        return null;
    }

    private Node createSubDir(Node dir, String name, String type, int candidate1, int candidate2) {

        //Random random = new Random();
        Node subDir = uniqueFactory.getOrCreate("name", name);
        index.add(subDir, "name", name);

        // Node subDir = dir.getGraphDatabase().createNode(); Create non unique
        // node
        NodeData metaData = new NodeData();
        metaData.setName(name);
        metaData.setCandidate1((type.equals("tally")) ? candidate1 : 0);
        metaData.setCandidate2((type.equals("tally")) ? candidate2 : 0);
        Gson gson = new Gson();
        subDir.setProperty("metadata", gson.toJson(metaData) );
        subDir.addLabel(Label.label(type));
        dir.createRelationshipTo(subDir, SUB_DIR);

        return subDir;
    }

    private void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

}
