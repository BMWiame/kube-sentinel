package dev.wiame.sentinel_api.observer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import dev.wiame.sentinel_api.dto.EventView;
import dev.wiame.sentinel_api.model.ScoredEvent;
@Component
public class BroadcastObserver implements EventObserver {
    private final SimpMessagingTemplate template;
    public BroadcastObserver(SimpMessagingTemplate template){
        this.template=template;
    }
    @Override
    public void onEvent(ScoredEvent e){
        EventView eventView = EventView.from(e);
        template.convertAndSend("/topic/events",eventView);
    }
}
