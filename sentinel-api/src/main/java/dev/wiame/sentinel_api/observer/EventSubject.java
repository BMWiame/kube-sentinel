package dev.wiame.sentinel_api.observer;

import java.util.concurrent.CopyOnWriteArrayList;
import dev.wiame.sentinel_api.model.ScoredEvent;
import java.util.List;
public class EventSubject {
    private final List<EventObserver> observers =new CopyOnWriteArrayList<>(); // thread safe
    public void subscribe(EventObserver o){observers.add(o);}
    public void unsubscribe(EventObserver o){observers.remove(o);}
    public void publish(ScoredEvent event){
        for(EventObserver observer:observers){observer.onEvent(event);}
    }
}
