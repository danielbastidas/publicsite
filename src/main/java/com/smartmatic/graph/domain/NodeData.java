package com.smartmatic.graph.domain;

import java.io.Serializable;

public class NodeData implements Serializable {

	
	private String name;
	
	private int candidate1;
	private int candidate2;
	
//	private BehaviorSubject<Integer> subject;
	
	public NodeData() {
//		subject = BehaviorSubject.createDefault(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
//		if (name.equals("Country")) {// the root node
//			subject.subscribe(val -> System.out.println("The value changed to:" + val));
//		}
	}

	public int getCandidate1() {
		return candidate1;
	}

	public void setCandidate1(int candidate1) {
		this.candidate1 = candidate1;
//		subject.onNext(candidate1);
	}

//	public BehaviorSubject<Integer> getSubject() {
//		return subject;
//	}
	
	public int getCandidate2() {
		return candidate2;
	}

	public void setCandidate2(int candidate2) {
		this.candidate2 = candidate2;
//		subject.onNext(candidate1);
	}

}
