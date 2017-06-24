package com.smartmatic.graph;

import java.io.File;
import java.util.Map;
import java.util.Random;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.UniqueFactory;

import com.smartmatic.graph.domain.NodeData;

// TODO: mejorar el rendimiento de la base de datos (configuracion)
// TODO: migrar codigo a gremlin
public class GraphTest {

	private GraphDatabaseService graphDb;

	private static final RelationshipType SUB_DIR = RelationshipType.withName("parent");

	private UniqueFactory.UniqueNodeFactory uniqueFactory;
	
	private Index<Node> index;

	// SessionFactory sessionFactory;

	public static void main(String[] args) {
		
		long t1 = System.currentTimeMillis();

		GraphTest obj = new GraphTest();

		obj.graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File("/home/danielbastidas/graphdb"))
				.setConfig(GraphDatabaseSettings.allow_store_upgrade, "false")
				.setConfig(GraphDatabaseSettings.cypher_compiler_tracing, "true")
				.setConfig(GraphDatabaseSettings.cypher_hints_error, "true")
				.setConfig(GraphDatabaseSettings.cypher_parser_version, "3.0").newGraphDatabase();
		
		String[][] matrix = new String[][] { { "C:Reg", "E1:Reg", "C1:Reg", "M1:Reg", "P1:Reg", "A1:tally" },
				{ "C:Reg", "E1:Reg", "C1:Reg", "M1:Reg", "P1:Reg", "A2:tally" },
				{ "C:Reg", "E1:Reg", "C2:Reg", "A3:tally" }, 
				{ "C:Reg", "E1:Reg", "C2:Reg", "A4:tally" } };

		PostSaveEventListener<Void> eventListener = new PostSaveEventListener<Void>();

		for (String[] tally : matrix) {

			try (Transaction tx = obj.graphDb.beginTx()) {
				
				obj.index = obj.graphDb.index().forNodes("Dir"); 
				
				obj.graphDb.registerTransactionEventHandler(eventListener);

				obj.uniqueFactory = new UniqueFactory.UniqueNodeFactory(obj.graphDb, "dirs") {
					@Override
					protected void initialize(Node created, Map<String, Object> properties) {
						created.addLabel(Label.label("Dir"));
						created.setProperty("name", properties.get("name"));
					}

					@Override
					protected Node create(Map<String, Object> properties) {
						// TODO Auto-generated method stub
						System.out.println("Creating node:" + properties.toString());
						return super.create(properties);
					}
				};

				// EventListener event = new PostSaveEventListener();
				//
				// SessionFactory sessionFactory = new
				// SessionFactory("com.smartmatic.graph");
				// sessionFactory.openSession().register(event);

				obj.storePath(tally);

				tx.success();

			}

		}

		obj.registerShutdownHook(obj.graphDb);
		System.out.println("Shutdown");
		System.out.println("Total time: "+(System.currentTimeMillis()-t1)+" ms.");

	}

	public void storePath(String[] path) {
		Node dir = uniqueFactory.getOrCreate("name", "C");// the root node
		String type = null;
		String name = null;
		for (String str : path) {
			name = str.split(":")[0];
			type = str.split(":")[1];
			dir = obtainSubDir(dir, name, type);
		}
	}

	private Node obtainSubDir(Node dir, String name, String type) {
		Node subDir = getSubDir(dir, name);
		if (subDir != null)
			return subDir;
		return createSubDir(dir, name, type);
	}

	private Node getSubDir(Node dir, String name) {
		for (Relationship rel : dir.getRelationships(SUB_DIR, Direction.OUTGOING)) {
			final Node subDir = rel.getEndNode();
			if (subDir.getProperty("name", "").equals(name))
				return subDir;
		}
		return null;
	}

	private Node createSubDir(Node dir, String name, String type) {

		Random random = new Random();
		Node subDir = uniqueFactory.getOrCreate("name", name);
		index.add(subDir, "name", name);
		
		// Node subDir = dir.getGraphDatabase().createNode(); Create non unique
		// node
		NodeData metaData = new NodeData();
		metaData.setName(name);
		metaData.setCounter((type.equals("tally")) ? random.nextInt(100) + 1 : 0);
		subDir.setProperty("metadata", metaData);
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