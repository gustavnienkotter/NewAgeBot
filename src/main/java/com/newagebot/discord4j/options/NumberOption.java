package com.newagebot.discord4j.options;

import discord4j.discordjson.json.ApplicationCommandOptionData;

public class NumberOption {

    private NumberOption() {}

    public static ApplicationCommandOptionData getOption() {
        return ApplicationCommandOptionData.builder().type(4).name("number").description("Number of dice sides").required(true).build();
    }
}
