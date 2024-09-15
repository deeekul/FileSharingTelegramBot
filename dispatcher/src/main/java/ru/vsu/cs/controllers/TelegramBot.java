package ru.vsu.cs.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vsu.cs.config.BotConfig;

import javax.annotation.PostConstruct;

/**
 * Class responsible for interacting with the telegram server
 */
@Log4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramWebhookBot {

    private final BotConfig botConfig;

    private final UpdateProcessor updateProcessor;

    @PostConstruct
    public void init() {
        updateProcessor.registerBot(this);
        try {
            var setWebhook = SetWebhook.builder()
                    .url(botConfig.getBotUri())
                    .build();
            this.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    /**
     * @return the path where telegram will send updates: <bot.uri> + "/callback" + "/update"
     */
    @Override
    public String getBotPath() {
        return "/update";
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }
}
