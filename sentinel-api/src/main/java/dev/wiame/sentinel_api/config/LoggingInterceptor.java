package dev.wiame.sentinel_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class LoggingInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        String destination = accessor.getDestination();

        log.info("command={}, destination={}", command, destination);

        if (StompCommand.SUBSCRIBE.equals(command)
                && destination != null
                && !destination.startsWith("/topic/")) {
            log.warn("Dropped SUBSCRIBE to forbidden destination: {}", destination);
            return null;
        }

        return message;
    }
}