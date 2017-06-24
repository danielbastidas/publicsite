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
			int counter = (Integer) nodeMetaData.getCounter();

			if (node.hasLabel(Label.label("tally"))) {

				Node parent = node.getSingleRelationship(RelationshipType.withName("parent"), Direction.INCOMING)
						.getStartNode();
				previous = node;

				while (parent != null && previous.equals(parent) == false) {
					
					NodeData parentMetaData = (NodeData) parent.getProperty("metadata");
					
					// increase counter for current branch
					parentMetaData.setCounter(parentMetaData.getCounter()+counter);
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