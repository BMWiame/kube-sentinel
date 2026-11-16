package dev.wiame.sentinel_api.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.util.List;
import dev.wiame.sentinel_api.observer.EventObserver;
import dev.wiame.sentinel_api.observer.EventSubject;
@Configuration
public class ObserverConfig {
    @Bean
    public EventSubject eventSubject (List<EventObserver> observers){
        EventSubject eventSubject = new EventSubject();
        observers.forEach(e->eventSubject.subscribe(e));
        return eventSubject;
    }

    
}
