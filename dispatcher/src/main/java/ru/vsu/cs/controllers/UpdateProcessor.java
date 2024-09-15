package ru.vsu.cs.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.cs.config.RabbitConfiguration;
import ru.vsu.cs.services.UpdateProducer;
import ru.vsu.cs.utils.MessageUtils;

/**
 * Class for distributing user messages
 */
@Log4j
@RequiredArgsConstructor
@Component
public class UpdateProcessor {

    private TelegramBot telegramBot;

    private final MessageUtils messageUtils;

    private final UpdateProducer updateProducer;

    private final RabbitConfiguration rabbitConfiguration;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Recieved update is null!");
        }

        if (update.hasMessage()) {
            distributeMessageByType(update);
        } else {
            log.error("Unsupported message type is recieved: " + update);
        }
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Unsupported message type!");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(rabbitConfiguration.getTextMessageUpdateQueue(), update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(rabbitConfiguration.getDocMessageUpdateQueue(), update);
        setFileIsRecievedView(update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(rabbitConfiguration.getPhotoMessageUpdateQueue(), update);
        setFileIsRecievedView(update);
    }

    private void setFileIsRecievedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Файл получен! Обрабатывается...");
        setView(sendMessage);
    }
}