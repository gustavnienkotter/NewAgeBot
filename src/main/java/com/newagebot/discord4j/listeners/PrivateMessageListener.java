package com.newagebot.discord4j.listeners;

import com.newagebot.discord4j.handlers.AbstractMessageHandler;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PrivateMessageListener {

    private final GatewayDiscordClient client;
    private final ApplicationContext applicationContext;

    public PrivateMessageListener(GatewayDiscordClient client, ApplicationContext applicationContext) {
        this.client = client;
        this.applicationContext = applicationContext;
        client.on(MessageCreateEvent.class, this::handle).subscribe();
    }

    private Mono<Object> handle(MessageCreateEvent event) {
        if (isSelfId(event)) {
            return Mono.empty();
        }
        String clazz = "supportChannel";
        if (isFromPrivateMessage(event)) {
            clazz = "privateMessage";
        }
        return ((AbstractMessageHandler) applicationContext.getBean(clazz)).handle(event);
    }

    private boolean isFromPrivateMessage(MessageCreateEvent event) {
        return event.getGuildId().isEmpty();
    }

    protected boolean isSelfId(MessageCreateEvent event) {
        return client.getSelfId().asLong() == (event.getMessage().getData().author().id().asLong());
    }

}
