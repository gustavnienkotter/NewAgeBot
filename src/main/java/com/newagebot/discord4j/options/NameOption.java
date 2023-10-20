package com.newagebot.discord4j.options;

import discord4j.discordjson.json.ApplicationCommandOptionData;

//TODO refatorar
public class NameOption {

    private NameOption() {}

    public static ApplicationCommandOptionData getOption() {
        return ApplicationCommandOptionData.builder().type(3).name("name").description("Your name").required(true).build();
    }

}
