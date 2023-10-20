package com.newagebot;

import com.newagebot.discord4j.commands.SlashCommand;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientCommandRegister implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestClient client;
    private final ApplicationContext applicationContext;

    public ClientCommandRegister(RestClient client, ApplicationContext applicationContext) {
        this.client = client;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        final ApplicationService applicationService = client.getApplicationService();
        final long applicationId = client.getApplicationId().blockOptional().orElseThrow();

        List<ApplicationCommandRequest> commands = new ArrayList<>();
        applicationContext.getBeansOfType(SlashCommand.class).values()
                .forEach(commandClass -> commands.add(addCommand(commandClass)));

        applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
                .doOnNext(ignore -> logger.debug("Successfully registered Global Commands"))
                .doOnError(e -> logger.error("Failed to register global commands", e))
                .subscribe();
    }

    private ApplicationCommandRequest addCommand(SlashCommand slashCommand) {
        return ApplicationCommandRequest.builder()
                .name(slashCommand.getName())
                .description(slashCommand.getDescription())
                .addAllOptions(slashCommand.getOptions())
                .build();
    }

}