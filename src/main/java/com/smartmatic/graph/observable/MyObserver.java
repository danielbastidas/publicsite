package com.smartmatic.graph.observable;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class MyObserver {

        private BehaviorSubject<String> subject;
        
        /*private BehaviourSubject subject1;
        
        private BehaviourSubject subject2;
        
        private Observable<Integer> observable;*/
        
        public MyObserver() {
              /*this.subject1 = BehaviourSubject.create();
              this.subject2 = BehaviourSubject.create();
              observable = BehaviourSubject.merge(this.subject1, this.subjec2);*/
              this.subject = BehaviorSubject.create();
        }
        
        /*public Observable<Integer> getObservable() {
                return observable;
        }
        
        public void setCandidate1(Integer value) {
                subject1.onNext(value);
        }
        
        public void setCandidate2(Integer value) {
                subject2.onNext(value);
        }*/
        
        public Observable<String> getObservable() {
                return subject;
        }
        
        public void setValue(String value) {
                subject.onNext(value);
        }

}
