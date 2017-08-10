package com.smartmatic.graph;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

public class LoadGraph {

	public static void main(String[] args) {
		
		long t1 = System.currentTimeMillis();

		GraphDatabaseService graphDb = new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder(new File("/home/danielbastidas/graphdb"))
				.setConfig(GraphDatabaseSettings.read_only, "true").newGraphDatabase();
		
		try (Transaction tx = graphDb.beginTx()) {
			
			
			// Find C
			Node C = graphDb.findNode(Label.label("Dir"), "name", "C" );

			// Find Michael's friends
			TraversalDescription myFriends = graphDb.traversalDescription()
			        .depthFirst()
			        .relationships( RelationshipType.withName("parent") );
			Traverser traverser = myFriends.traverse( C );
			System.out.println( "Tree bread first: " );
			for (Node subDir : traverser.nodes()) {
				System.out.println("\t" + subDir.getProperty("name") + " - " + subDir.getProperty("counter"));
			}
			
//			String query0 = "match (dir:Dir)-[:child]->(subdir:SubDir) return dir";
//			Result result0 = graphDb.execute(query0);
//			System.out.println(result0.resultAsString());
//			
//			for (Iterator iterator = graphDb.getAllNodes().iterator(); iterator.hasNext();) {
//				Node node = (Node) iterator.next();
//				Map<String, Object> map = node.getAllProperties();
//				System.out.println(map.get("name"));
//			}
//			
//			System.out.println("Relationships");
//			
//			for (Iterator iterator = graphDb.getAllRelationships().iterator(); iterator.hasNext();) {
//				Relationship relationship = (Relationship) iterator.next();
//				System.out.println(relationship.toString());
//			}
//			
//			String query = "MATCH (n) RETURN n;";
//			
//			Result result = graphDb.execute(query);
//			
//			if (result != null && result.hasNext()) {
//				System.out.println(result.resultAsString());
//			} else {
//				System.out.println("No tiene resultados");
//			}
			
//			System.out.println(graphDb.getAllNodes().iterator().next().toString());
			System.out.println("Total time: "+(System.currentTimeMillis()-t1)+" ms.");
			System.out.println("Done.");
			
		} finally {
			
		}
		
	}

}
