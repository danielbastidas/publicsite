package com.smartmatic.graph;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.reactivex.functions.Consumer;
import org.reactivestreams.Subscription;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Arrays;

public class MySubscriber implements Consumer<String> {

    private Session session;

    private String regionName;

    private String[] regionChildren;

    public MySubscriber(Session session, String regionName, String[] childs) {
        this.session = session;
        this.regionName = regionName;
        this.regionChildren = childs;
    }

    //@Override
    public void onComplete() {
    }

    //@Override
    public void onSubscribe(Subscription subscription) {

    }

    //@Override
//    public void onNext(String value) {
//
//        String candidate1 = value.split("|")[0].split(":")[1];
//        String candidate2 = value.split("|")[1].split(":")[1];
//
//        JsonObject json = new JsonObject();
//        JsonArray jsonArray = new JsonArray();
//
//        Arrays.stream(regionChildren).forEach(s -> jsonArray.add(s));
//
//        json.addProperty("regionName",  regionName);
//        json.add("children", jsonArray);
//        json.addProperty("candidate1", candidate1);
//        json.addProperty("candidate2", candidate2);
//
//        try {
//            session.getBasicRemote().sendText(json.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //@Override
    public void onError(Throwable t) {
    }

    @Override
    public void accept(String value) throws Exception {

        String candidate1 = value.split("\\|")[0].split(":")[1];
        String candidate2 = value.split("\\|")[1].split(":")[1];

        //ResultDTO resultDTO = new ResultDTO(regionName, candidate1, candidate2);

        JsonObject json = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        if (regionChildren != null) {
            Arrays.stream(regionChildren).forEach(s -> jsonArray.add(s));
        }

        json.addProperty("regionName",  regionName);
        json.add("children", jsonArray);
        json.addProperty("candidate1", candidate1);
        json.addProperty("candidate2", candidate2);

        try {
            session.getBasicRemote().sendText(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
