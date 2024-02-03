package com.newagebot.discord4j.handlers;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateMono;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AbstractMessageHandler {

    @Value("${SUPPORT_ID}")
    protected String supportChannelId;

    @Value("${GUILD_ID}")
    protected String guildId;

    protected final GatewayDiscordClient client;

    public Mono<Object> handle(MessageCreateEvent event) {
        return Mono.empty();
    }

    protected MessageCreateMono createMessage(MessageChannel channel, String message, List<Attachment> attachments) {
        StringBuilder attachmentText = new StringBuilder();
        if (!attachments.isEmpty()) {
            attachmentText.append("\n\n**Arquivos enviados:**");
            attachments.forEach(attachment -> attachmentText.append("\n").append(attachment.getUrl()));
        }
        return channel.createMessage(message + attachmentText);
    }

    protected Mono<Void> addEmote(MessageCreateEvent event, String emote) {
        return event.getMessage().addReaction(ReactionEmoji.unicode(emote));
    }
}
