package ru.vsu.cs.services.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.vsu.cs.entities.AppUser;
import ru.vsu.cs.entities.RawData;
import ru.vsu.cs.repositories.AppUserRepository;
import ru.vsu.cs.repositories.RawDataRepository;
import ru.vsu.cs.services.MainService;
import ru.vsu.cs.services.ProducerService;

import java.util.Optional;

import static ru.vsu.cs.enums.UserState.BASIC_STATE;
import static ru.vsu.cs.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.vsu.cs.services.enums.ServiceCommands.*;

/**
 * The service through which incoming messages will be processed
 */
@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;

    public MainServiceImpl(RawDataRepository rawDataRepository, ProducerService producerService, AppUserRepository appUserRepository) {
        this.rawDataRepository = rawDataRepository;
        this.producerService = producerService;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var answerMsg = "";

        if (CANCEL.equals(text)) {
            answerMsg = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            answerMsg = processServiceCommands(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            // TODO добавить обработку email
        } else {
            log.debug("Unknown user state: " + userState);
            answerMsg = "Unknown error! Input /cancel and try again!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(answerMsg, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        // TODO Добавить сохранение документа
        var answerMessage = "The document has been uploaded successfully! Download link: http://test.ru/get-doc/777";
        sendAnswer(answerMessage, chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        // TODO Добавить сохранение фото
        var answerMessage = "The photo has been uploaded successfully! Download link: http://test.ru/get-photo/777";
        sendAnswer(answerMessage, chatId);
    }

    private boolean isNotAllowedToSendContent(Long chatId, AppUser appUser) {
        var errorMsg = "";
        var userState = appUser.getState();
        if (!appUser.isActive()) {
            errorMsg = "Register or activate your account to download content!";
            sendAnswer(errorMsg, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            errorMsg = "Cancel the current command using the /cancel command to send files!";
            sendAnswer(errorMsg, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommands(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)) {
            // TODO добавить регистрацию
            return "Temporarily unavailable!";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Greetings! To view the list of available commands, type /help";
        } else {
            return "Unknown command! To view the list of available commands, type /help";
        }
    }

    private String help() {
        return "List of available commands:\n"
                + "/cancel - canceling the execution of the current command;\n"
                + "/registration - registration of user;\n";
    }

    private void registrationProcess(AppUser appUser) {

    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserRepository.save(appUser);
        return "Command has been canceled!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> persistentAppUser = appUserRepository.findAppUserByTelegramUserId(telegramUser.getId());
        if (!persistentAppUser.isPresent()) {
            AppUser transientAppUser = AppUser.builder()
                                .telegramUserId(telegramUser.getId())
                                .userName(telegramUser.getUserName())
                                .firstName(telegramUser.getFirstName())
                                .lastName(telegramUser.getLastName())
                                .isActive(true) // TODO поменять значение по умолчанию после регистрации
                                .state(BASIC_STATE)
                                .build();
            return appUserRepository.save(transientAppUser);
        }
        return persistentAppUser.get();
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                        .event(update)
                        .build();
        rawDataRepository.save(rawData);
    }
}
