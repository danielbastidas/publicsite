package com.smartmatic.graph.service;

import com.smartmatic.graph.observable.MyObserver;
import com.smartmatic.graph.spec.IObserver;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ObserverBean implements IObserver {

    private Map<String, MyObserver> observers = new HashMap<String, MyObserver>();

    @Lock(LockType.READ)
    public MyObserver getObserver(String regionName) {
        return observers.get(regionName);
    }

    @Lock(LockType.WRITE)
    public void addObserver(MyObserver observer, String regionName) {
        observers.put(regionName, observer);
    }

}
