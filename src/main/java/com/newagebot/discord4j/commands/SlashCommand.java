package com.newagebot.discord4j.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

public interface SlashCommand {

    Random RANDOM = new Random();

    String getName();

    String getDescription();

    Mono<Void> handle(ChatInputInteractionEvent event);

    default List<ApplicationCommandOptionData> getOptions() {
        return List.of();
    };
}
