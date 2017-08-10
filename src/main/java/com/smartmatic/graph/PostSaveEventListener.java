package com.smartmatic.graph;

import com.google.gson.Gson;
import com.smartmatic.graph.domain.NodeData;
import com.smartmatic.graph.observable.MyObserver;
import com.smartmatic.graph.spec.IObserver;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Iterator;
import java.util.Properties;

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
        Gson gson = new Gson();

        for (Iterator iterator = data.createdNodes().iterator(); iterator.hasNext(); ) {

            Node node = (Node) iterator.next();
            NodeData nodeMetaData = (NodeData) gson.fromJson(node.
                    getProperty("metadata").toString(), NodeData.class);
            int candidate1 = (Integer) nodeMetaData.getCandidate1();
            int candidate2 = (Integer) nodeMetaData.getCandidate2();
            String regionName = (String) nodeMetaData.getName();

            if (node.hasLabel(Label.label("tally"))) {

                Node parent = node.getSingleRelationship(RelationshipType.withName("parent"), Direction.INCOMING)
                        .getStartNode();
                previous = node;

                MyObserver tallyObserver = new MyObserver();
                tallyObserver.setValue("C1:" + candidate1 + "|C2:" + candidate2);

                // Set up the context for the JNDI lookup

                Properties prop = new Properties();
                prop.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
                prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
//                prop.put("jboss.naming.client.ejb.context", true);
//                prop.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

                Context context = new InitialContext(prop);

                IObserver observerBean = (IObserver) context.
                        lookup("java:app/real-time-public-site/ObserverBean!com.smartmatic.graph.spec.IObserver");
                observerBean.addObserver(tallyObserver, regionName);

                while (parent != null && previous.equals(parent) == false) {

                    NodeData parentMetaData = (NodeData) gson.fromJson(parent.
                            getProperty("metadata").toString(), NodeData.class);

                    // increase counter for current branch
                    regionName = parentMetaData.getName();
                    parentMetaData.setCandidate1(parentMetaData.getCandidate1() + candidate1);
                    parentMetaData.setCandidate2(parentMetaData.getCandidate2() + candidate2);

                    parent.setProperty("metadata", gson.toJson(parentMetaData));

                    MyObserver observer = observerBean.getObserver(regionName);
                    if (observer == null) {
                        MyObserver regionObserver = new MyObserver();
                        regionObserver.setValue("C1:" + parentMetaData.getCandidate1() + "|C2:" + parentMetaData.getCandidate2());
                        observerBean.addObserver(regionObserver, regionName);
                    } else {
                        observer.setValue("C1:" + parentMetaData.getCandidate1() + "|C2:" + parentMetaData.getCandidate2());
                    }

                    previous = parent;
                    parent = parent.getSingleRelationship(RelationshipType.withName("parent"), Direction.INCOMING)
                            .getStartNode();
                }

            }

        }

        return null;
    }

}
