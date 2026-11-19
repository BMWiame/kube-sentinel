package dev.wiame.sentinel_api.observer;
import dev.wiame.sentinel_api.model.ScoredEvent;

public interface EventObserver {
    public void onEvent(ScoredEvent e);
}
