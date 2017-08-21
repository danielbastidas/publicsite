package com.smartmatic.graph.spec;

import com.smartmatic.graph.observable.MyObserver;

import javax.ejb.Local;

@Local
public interface IObserver {

        MyObserver getObserver(String regionName);
        
        void addObserver(MyObserver observer, String regionName);
}
