package com.smartmatic.graph;

import com.google.gson.Gson;
import com.smartmatic.graph.domain.NodeData;
import com.smartmatic.graph.observable.MyObserver;
import com.smartmatic.graph.service.ObserverBean;
import com.smartmatic.graph.spec.IObserver;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Iterator;

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

                IObserver observerBean = lookSessionBean();

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

    private IObserver lookSessionBean() throws NamingException {

        final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
        // The app name is the application name of the deployed EJBs. This is typically the ear name
        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
        // EJB deployment on the server.
        // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
        final String appName = "";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
        // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
        // jboss-as-ejb-remote-app
        final String moduleName = "real-time-public-site";
        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = ObserverBean.class.getSimpleName();
        // the remote view fully qualified class name
        final String viewClassName = IObserver.class.getName();
        // let's do the lookup
        return (IObserver) context.lookup("java:global/" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);

    }

}
