package com.newagebot.discord4j.handlers;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.MessageData;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component("supportChannel")
public class SupportChannelMessageHandler extends AbstractMessageHandler {

    public SupportChannelMessageHandler(GatewayDiscordClient client) {
        super(client);
    }

    @Override
    public Mono<Object> handle(MessageCreateEvent event) {
        if (!isFromSupportChannel(event)){
            return Mono.empty();
        }
        return event.getMessage().getChannel().flatMap(messageChannel -> {
            if (!messageChannel.getId().equals(Snowflake.of(supportChannelId)) || event.getGuildId().isEmpty()) {
                return Mono.empty();
            }
            if (event.getMessage().getContent().startsWith("<@")) {
                return handleMentionReply(event);

            }
            return handleReferencedMessageReply(event);
        });
    }

    private boolean isFromSupportChannel(MessageCreateEvent event) {
        return event.getMessage().getChannelId().equals(Snowflake.of(supportChannelId));
    }

    private Mono<Object> handleMentionReply(MessageCreateEvent event) {
        return client.getUserById(event.getMessage().getMemberMentions().get(0).getId())
                .flatMap(User::getPrivateChannel)
                .flatMap(channel -> createMessage(channel, getMessage(event), event.getMessage().getAttachments())
                        .then(addEmote(event,"✅")));
    }

    private Mono<Object> handleReferencedMessageReply(MessageCreateEvent event) {
        if (event.getMessage().getData().referencedMessage().get().isEmpty()) {
            return Mono.empty();
        }
        return client.getUserById(Snowflake.of(getReferencedUser(event.getMessage().getData().referencedMessage().get().get())))
                .flatMap(User::getPrivateChannel)
                .flatMap(channel -> createMessage(channel, event.getMessage().getContent().trim(), event.getMessage().getAttachments())
                        .then(addEmote(event,"✅")));
    }

    private String getReferencedUser(MessageData messageData) {
        return Arrays.stream(messageData.content().split(">")).toList().get(0).replace("<@", "");
    }

    private String getMessage(MessageCreateEvent event) {
        return Arrays.stream(event.getMessage().getContent().split("^(<@)+([0-9])+(>)")).toList().get(1);
    }
}
