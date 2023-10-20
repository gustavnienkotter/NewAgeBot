package com.newagebot.discord4j.listeners;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateMono;
import discord4j.discordjson.json.MessageData;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class PrivateMessageListener {

    GatewayDiscordClient client;

    public PrivateMessageListener(GatewayDiscordClient client) {
        this.client = client;
        client.on(MessageCreateEvent.class, this::handle).subscribe();
    }

    private Mono<Object> handle(MessageCreateEvent event) {
        if (isSelfId(event)) {
            return Mono.empty();
        }
        if (event.getGuildId().isEmpty()) {
            return client.getGuildById(Snowflake.of(1121476730851446804L))
                    .flatMap(this::getSupportChannel)
                    .flatMap(channel -> createMessage(channel, "<@" + event.getMessage().getData().author().id() + "> : " + event.getMessage().getContent(), event.getMessage().getAttachments())
                            .then(event.getMessage().addReaction(ReactionEmoji.unicode("✅"))));
        }
        return event.getMessage().getChannel().flatMap(messageChannel -> {
            if (!messageChannel.getId().equals(Snowflake.of(1164901922143215676L))) {
                return Mono.empty();
            }
            if (event.getGuildId().isPresent() && event.getMessage().getContent().startsWith("<@")) {
                List<String> message = Arrays.stream(event.getMessage().getContent().split("^(<@)+([0-9])+(>)")).toList();
                return client.getUserById(event.getMessage().getMemberMentions().get(0).getId())
                        .flatMap(User::getPrivateChannel)
                        .flatMap(channel -> createMessage(channel, message.get(1).trim(), event.getMessage().getAttachments()).then(event.getMessage().addReaction(ReactionEmoji.unicode("✅"))));
            } else if (event.getGuildId().isPresent() && !event.getMessage().getData().referencedMessage().isAbsent()) {
                MessageData messageData = event.getMessage().getData().referencedMessage().get().get();
                List<String> message = Arrays.stream(messageData.content().split(">")).toList();
                return client.getUserById(Snowflake.of(message.get(0).replace("<@", "")))
                        .flatMap(User::getPrivateChannel)
                        .flatMap(channel -> createMessage(channel, event.getMessage().getContent().trim(), event.getMessage().getAttachments()).then(event.getMessage().addReaction(ReactionEmoji.unicode("✅"))));
            }
            return Mono.empty();
        });
    }

    private MessageCreateMono createMessage(MessageChannel channel, String message, List<Attachment> attachments) {
        StringBuilder attachmentText = new StringBuilder();
        if (!attachments.isEmpty()) {
            attachmentText.append("\n\n**Arquivos enviados:**");
            attachments.forEach(attachment -> attachmentText.append("\n").append(attachment.getUrl()));
        }
        return channel.createMessage(message + attachmentText);
    }

    private boolean isSelfId(MessageCreateEvent event) {
        return client.getSelfId().asLong() == (event.getMessage().getData().author().id().asLong());
    }

    private Mono<MessageChannel> getSupportChannel(Guild guild) {
        return guild.getChannelById(Snowflake.of(1164901922143215676L)).ofType(MessageChannel.class);
    }

}
