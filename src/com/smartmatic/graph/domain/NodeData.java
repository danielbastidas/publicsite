package com.smartmatic.graph.domain;

import java.io.Serializable;

import io.reactivex.subjects.BehaviorSubject;

public class NodeData implements Serializable {

	
	private String name;
	
	private int counter;
	
	private BehaviorSubject<Integer> subject;
	
	public NodeData() {
		subject = BehaviorSubject.createDefault(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if (name.equals("C")) {// the root node
			subject.subscribe(val -> System.out.println("The value changed to:" + val));
		}
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
		subject.onNext(counter);
	}

	public BehaviorSubject<Integer> getSubject() {
		return subject;
	}

}
