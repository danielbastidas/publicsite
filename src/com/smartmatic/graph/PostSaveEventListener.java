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
				
				// Set up the context for the JNDI lookup
            			Properties prop = new Properties();
				prop.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
				Context context = new InitialContext(prop);
				
				IObserver observerBean = (IObserver) context.lookup("ejb:/myapp/remote/calculator!de.akquinet.jbosscc.ejb.ObserverBean");
				observerBean.addObserver(tallyObserver, regionName);

				while (parent != null && previous.equals(parent) == false) {
					
					NodeData parentMetaData = (NodeData) parent.getProperty("metadata");
					
					// increase counter for current branch
					regionName = parentMetaData.getName();
					parentMetaData.setCandidate1(parentMetaData.getCandidate1()+candidate1);
					parentMetaData.setCandidate2(parentMetaData.getCandidate2()+candidate2);
					
					Observable observer = observerBean.get(regionName);
					if (observer == null) {
						MyObserver regionObserver = new MyObserver();
						regionObserver.setCandidate1(parentMetaData.getCandidate1());
						regionObserver.setCandidate2(parentMetaData.getCandidate2());
						observerBean.addObserver(regionObserver, regionName);
					} else {
						observer.setCandidate1(parentMetaData.getCandidate1());
						observer.setCandidate2(parentMetaData.getCandidate2());
					}
					
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
