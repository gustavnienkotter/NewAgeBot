package com.newagebot.discord4j.options;

import discord4j.discordjson.json.ApplicationCommandOptionData;

//TODO refatorar
public class EphemeralOption {

    private EphemeralOption() {}

    public static ApplicationCommandOptionData getOption() {
        return ApplicationCommandOptionData.builder().type(5).name("anonymous").description("Needs your response in anonymous mode").required(false).build();
    }

}