package dev.wiame.sentinel_api.observer;

import dev.wiame.sentinel_api.model.IngestedEvent;
import dev.wiame.sentinel_api.model.SecurityEvents;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventSubjectTest {

    @Test
    void publishNotifiesSubscribedObserver() {
        EventSubject subject = new EventSubject();
        List<IngestedEvent> received = new ArrayList<>();

        subject.subscribe(event -> received.add(event));

        SecurityEvents securityEvent =
                new SecurityEvents("shell in container", "HIGH", "demo", "victim", Instant.now());
        IngestedEvent ingested = new IngestedEvent(securityEvent, "{\"rule\":\"shell in container\"}");

        subject.publish(ingested);

        assertThat(received).hasSize(1);
    }

    @Test
    void publishNotifiesAllSubscribedObservers() {
        EventSubject subject = new EventSubject();
        List<IngestedEvent> firstReceived = new ArrayList<>();
        List<IngestedEvent> secondReceived = new ArrayList<>();

        subject.subscribe(event -> firstReceived.add(event));
        subject.subscribe(event -> secondReceived.add(event));

        IngestedEvent ingested = new IngestedEvent(
                new SecurityEvents("shell in container", "HIGH", "demo", "victim", Instant.now()),
                "{}");

        subject.publish(ingested);

        assertThat(firstReceived).hasSize(1);
        assertThat(secondReceived).hasSize(1);
    }

    @Test
    void unsubscribedObserverReceivesNothing() {
        EventSubject subject = new EventSubject();
        List<IngestedEvent> received = new ArrayList<>();
        EventObserver observer = event -> received.add(event);

        subject.subscribe(observer);
        subject.unsubscribe(observer);

        IngestedEvent ingested = new IngestedEvent(
                new SecurityEvents("shell in container", "HIGH", "demo", "victim", Instant.now()),
                "{}");

        subject.publish(ingested);

        assertThat(received).isEmpty();
    }
}