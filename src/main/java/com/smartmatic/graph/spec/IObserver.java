package com.smartmatic.graph.spec;

import com.smartmatic.graph.observable.MyObserver;

import javax.ejb.Local;

@Local
public interface IObserver {

        public MyObserver getObserver(String regionName);
        
        public void addObserver(MyObserver observer, String regionName);
}
