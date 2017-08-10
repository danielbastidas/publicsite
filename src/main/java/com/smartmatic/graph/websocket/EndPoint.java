package com.smartmatic.graph.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartmatic.graph.MySubscriber;
import com.smartmatic.graph.observable.MyObserver;
import com.smartmatic.graph.spec.IObserver;
import com.smartmatic.graph.spec.IRegion;
import io.reactivex.disposables.Disposable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/graphWebSocket")
@Stateless
public class EndPoint {

    private final static String SUBSCRIBER_KEY = "SUBSCRIBER";

    @EJB
    private IObserver observerBean;

    @EJB
    private IRegion regionBean;

    @OnMessage
    public String receiveMessage(String message, Session session) {

        JsonObject jObject = (JsonObject) new JsonParser().parse(message);
        String type = jObject.get("type").getAsString();
        String region = jObject.get("region").getAsString();

        if (type.compareToIgnoreCase("subscribe") == 0) {

            if (region == null) {
                // first time the user land on the home page
                region = "Country";
            }

            String[] children = regionBean.getChildren(region);
            int index = 0;

            do {

                MySubscriber subscriber = new MySubscriber(session, region, (index > 0)? null:children);
                MyObserver observable = observerBean.getObserver(region);

                if (observable != null) {
                    observable.getObservable().subscribe(subscriber);
                } else {
                    observable = new MyObserver();
                    observable.setValue("C1:" + 0 + "|C2:" + 0);
                    observable.getObservable().subscribe(subscriber);
                    observerBean.addObserver(observable, region);
                }
                session.getUserProperties().put(SUBSCRIBER_KEY, subscriber);

                if (children != null && index < children.length) {
                    region = children[index++];
                } else {
                    region = null;
                }

            } while (region != null);

        } else if (type.compareToIgnoreCase("unsubscribe") == 0) {
            Disposable subscriber = (Disposable) session.getUserProperties().get(SUBSCRIBER_KEY);
            if (subscriber != null) {
                // unsubscribe to avoid subscribers leak
                subscriber.dispose();
            }
        }

        return "DONE";
    }

    @OnOpen
    public void open(final Session session) throws IOException {
        System.out.println("Session opened:" + session.getId());
    }

    @OnClose
    public void close(Session session, CloseReason c) {
        System.out.println("Session closed:" + session.getId());
    }

}
