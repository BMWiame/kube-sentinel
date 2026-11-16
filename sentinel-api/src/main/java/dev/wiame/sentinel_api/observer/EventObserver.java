package dev.wiame.sentinel_api.observer;
import dev.wiame.sentinel_api.model.IngestedEvent;

public interface EventObserver {
    public void onEvent(IngestedEvent e);
}
