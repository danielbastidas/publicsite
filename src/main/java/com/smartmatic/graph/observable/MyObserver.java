package com.smartmatic.graph.observable;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class MyObserver {

        private BehaviorSubject<String> subject;
        
        public MyObserver() {
              /*this.subject1 = BehaviourSubject.create();
              this.subject2 = BehaviourSubject.create();
              observable = BehaviourSubject.merge(this.subject1, this.subjec2);*/
              this.subject = BehaviorSubject.create();
        }
        
        public Observable<String> getObservable() {
                return subject;
        }
        
        public void setValue(String value) {
                subject.onNext(value);
        }

}
