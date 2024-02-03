package com.newagebot.discord4j.handlers;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("privateMessage")
public class PrivateMessageHandler extends AbstractMessageHandler {

    public PrivateMessageHandler(GatewayDiscordClient client) {
        super(client);
    }

    @Override
    public Mono<Object> handle(MessageCreateEvent event) {
        return client.getGuildById(Snowflake.of(guildId))
                .flatMap(this::getSupportChannel)
                .flatMap(channel -> createMessage(channel, getMessage(event), event.getMessage().getAttachments())
                        .then(event.getMessage().addReaction(ReactionEmoji.unicode("✅"))));
    }

    private String getMessage(MessageCreateEvent event) {
        return "<@" + event.getMessage().getData().author().id() + "> : " + event.getMessage().getContent();
    }

    private Mono<MessageChannel> getSupportChannel(Guild guild) {
        return guild.getChannelById(Snowflake.of(supportChannelId)).ofType(MessageChannel.class);
    }
}
