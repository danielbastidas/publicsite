package com.smartmatic.graph;

import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

import com.smartmatic.graph.domain.NodeData;

public class PostSaveEventListener<Void> implements TransactionEventHandler {

	@Override
	public void afterCommit(TransactionData data, Object arg1) {

	}

	@Override
	public void afterRollback(TransactionData data, Object arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public Void beforeCommit(TransactionData data) throws Exception {
		
		Node previous = null;

		for (Iterator iterator = data.createdNodes().iterator(); iterator.hasNext();) {

			Node node = (Node) iterator.next();
			NodeData nodeMetaData = (NodeData) node.getProperty("metadata");
			int candidate1 = (Integer) nodeMetaData.getCandidate1();
			int candidate2 = (Integer) nodeMetaData.getCandidate2();
			String regionName = (String) nodeMetaData.getName();

			if (node.hasLabel(Label.label("tally"))) {

				Node parent = node.getSingleRelationship(RelationshipType.withName("parent"), Direction.INCOMING)
						.getStartNode();
				previous = node;
				
				MyObserver tallyObserver = new MyObserver();
				tallyObserver.setCandidate1(candidate1);
				tallyObserver.setCandidate2(candidate2);
				//TODO inyectar una instancia del singletonbean para poner el observer en el mapa por region

				while (parent != null && previous.equals(parent) == false) {
					
					NodeData parentMetaData = (NodeData) parent.getProperty("metadata");
					
					// increase counter for current branch
					parentMetaData.setCandidate1(parentMetaData.getCandidate1()+candidate1);
					parentMetaData.setCandidate2(parentMetaData.getCandidate2()+candidate2);
//					parent.setProperty("counter", (Integer) parent.getProperty("counter") + counter);
					previous=parent;
					parent = parent.getSingleRelationship(RelationshipType.withName("parent"), Direction.INCOMING)
							.getStartNode();
				}

			}

		}

		return null;
	}

}
