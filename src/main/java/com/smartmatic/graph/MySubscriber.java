package com.smartmatic.graph;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Arrays;

public class MySubscriber implements Consumer<String>, Disposable {

    private Session session;

    private String regionName;

    private String[] regionChildren;

    private String parentRegion;

    public MySubscriber(Session session, String regionName, String parentRegion,
                        String[] children) {
        this.session = session;
        this.regionName = regionName;
        this.regionChildren = children;
        this.parentRegion = parentRegion;
    }

    @Override
    public void accept(String value) throws Exception {

        String candidate1 = value.split("\\|")[0].split(":")[1];
        String candidate2 = value.split("\\|")[1].split(":")[1];

        System.out.println("Sending values from subscriber ["+regionName+"] - "+candidate1+" "+candidate2);

        JsonObject json = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        if (regionChildren != null) {
            Arrays.stream(regionChildren).forEach(s -> jsonArray.add(s));
        }

        json.addProperty("regionName",  regionName);
        json.add("children", jsonArray);
        json.addProperty("candidate1", candidate1);
        json.addProperty("candidate2", candidate2);
        json.addProperty("parentRegion",  parentRegion);

        try {
            session.getBasicRemote().sendText(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dispose() {
        System.out.println("Terminating subscriber for region ["+regionName+"]");
    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
