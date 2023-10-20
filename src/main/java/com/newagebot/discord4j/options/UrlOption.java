package com.newagebot.discord4j.options;

import discord4j.discordjson.json.ApplicationCommandOptionData;

//TODO refatorar
public class UrlOption {

    private UrlOption() {}

    public static ApplicationCommandOptionData getOption() {
        return ApplicationCommandOptionData.builder().type(3).name("url").description("Music URL").required(true).build();
    }

}
