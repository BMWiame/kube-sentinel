package dev.wiame.sentinel_api.observer;

import dev.wiame.sentinel_api.model.ScoredEvent;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventSubjectTest {

    @Test
    void publishNotifiesSubscribedObserver() {
        EventSubject subject = new EventSubject();
        List<ScoredEvent> received = new ArrayList<>();

        subject.subscribe(event -> received.add(event));

        SecurityEvents securityEvent =
                new SecurityEvents("shell in container", "HIGH", "demo", "victim", Instant.now());
        ScoredEvent scored = new ScoredEvent(securityEvent, Severity.LOW, "{\"rule\":\"shell in container\"}");

        subject.publish(scored);

        assertThat(received).hasSize(1);
    }

    @Test
    void publishNotifiesAllSubscribedObservers() {
        EventSubject subject = new EventSubject();
        List<ScoredEvent> firstReceived = new ArrayList<>();
        List<ScoredEvent> secondReceived = new ArrayList<>();

        subject.subscribe(event -> firstReceived.add(event));
        subject.subscribe(event -> secondReceived.add(event));

        ScoredEvent scored = new ScoredEvent(
                new SecurityEvents("shell in container", "HIGH", "demo", "victim", Instant.now()),
                Severity.LOW,
                "{}");

        subject.publish(scored);

        assertThat(firstReceived).hasSize(1);
        assertThat(secondReceived).hasSize(1);
    }

    @Test
    void unsubscribedObserverReceivesNothing() {
        EventSubject subject = new EventSubject();
        List<ScoredEvent> received = new ArrayList<>();
        EventObserver observer = event -> received.add(event);

        subject.subscribe(observer);
        subject.unsubscribe(observer);

        ScoredEvent scored = new ScoredEvent(
                new SecurityEvents("shell in container", "HIGH", "demo", "victim", Instant.now()),
                Severity.LOW,
                "{}");

        subject.publish(scored);

        assertThat(received).isEmpty();
    }
}