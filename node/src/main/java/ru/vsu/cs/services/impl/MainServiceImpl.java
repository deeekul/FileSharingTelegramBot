package ru.vsu.cs.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.AppPhoto;
import ru.vsu.cs.entities.AppUser;
import ru.vsu.cs.entities.RawData;
import ru.vsu.cs.exceptions.UploadFileException;
import ru.vsu.cs.repositories.AppUserRepository;
import ru.vsu.cs.repositories.RawDataRepository;
import ru.vsu.cs.services.AppUserService;
import ru.vsu.cs.services.FileService;
import ru.vsu.cs.services.MainService;
import ru.vsu.cs.services.ProducerService;
import ru.vsu.cs.services.enums.LinkType;
import ru.vsu.cs.services.enums.ServiceCommand;

import static ru.vsu.cs.enums.UserState.BASIC_STATE;
import static ru.vsu.cs.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.vsu.cs.services.enums.ServiceCommand.*;

/**
 * The service through which incoming messages will be processed
 */
@Service
@RequiredArgsConstructor
@Log4j
public class MainServiceImpl implements MainService {

    private final RawDataRepository rawDataRepository;

    private final AppUserRepository appUserRepository;

    private final ProducerService producerService;

    private final FileService fileService;

    private final AppUserService appUserService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var answerMsg = "";

        var serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            answerMsg = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            answerMsg = processServiceCommands(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            answerMsg = appUserService.setEmail(appUser, text);
        } else {
            log.debug("Неизвестное состояние пользователя: " + userState);
            answerMsg = "Неизвестная ошибка! Введите /cancel и повторите попытку!";
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

        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
            var answerMessage = "Документ успешно загружен! "
                    + "Ссылка для скачивания: " + link;
            sendAnswer(answerMessage, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String errorMessage = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(errorMessage, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answerMessage = "Документ успешно загружен! "
                    + "Ссылка для скачивания: " + link;
            sendAnswer(answerMessage, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String errorMessage = "К сожалению, загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(errorMessage, chatId);
        }
    }

    private boolean isNotAllowedToSendContent(Long chatId, AppUser appUser) {
        var errorMsg = "";
        var userState = appUser.getState();
        if (!appUser.isActive()) {
            errorMsg = "Зарегистрируйте или активируйте свою учетную запись для загрузки контента!";
            sendAnswer(errorMsg, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            errorMsg = "Отмените текущую команду, используя команду /cancel для отправки файлов!";
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
        var serviceCommand = ServiceCommand.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (START.equals(serviceCommand)) {
            return "Привет! Чтобы просмотреть список доступных команд, введите /help";
        } else {
            return "Неизвестная команда! Чтобы просмотреть список доступных команд, введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/registration - регистрация пользователя.\n";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserRepository.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        var telegramUser = update.getMessage().getFrom();
        var appUserOpt = appUserRepository.findByTelegramUserId(telegramUser.getId());
        if (appUserOpt.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserRepository.save(transientAppUser);
        }
        return appUserOpt.get();
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }
}
