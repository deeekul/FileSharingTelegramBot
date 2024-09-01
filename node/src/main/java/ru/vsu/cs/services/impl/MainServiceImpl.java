package ru.vsu.cs.services.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.vsu.cs.entities.AppUser;
import ru.vsu.cs.enums.UserState;
import ru.vsu.cs.repositories.AppUserRepository;
import ru.vsu.cs.repositories.RawDataRepository;
import ru.vsu.cs.entities.RawData;
import ru.vsu.cs.services.MainService;
import ru.vsu.cs.services.ProducerService;

import static ru.vsu.cs.enums.UserState.BASIC_STATE;

/**
 * The service through which incoming messages will be processed
 */
@Service
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
    public void processTextMessages(Update update) {
        saveRawData(update);
        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        findOrSaveAppUser(telegramUser);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(textMessage.getChatId().toString());
        sendMessage.setText("Hello from NODE");
        producerService.producerAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(User telegramUser) {
        AppUser persistentAppUser = appUserRepository.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                                .telegramUserId(telegramUser.getId())
                                .userName(telegramUser.getUserName())
                                .firstName(telegramUser.getFirstName())
                                .lastName(telegramUser.getLastName())
                                .isActive(true) // TODO поменять значение по умолчанию после регистрации
                                .userState(BASIC_STATE)
                                .build();
            return appUserRepository.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                        .event(update)
                        .build();
        rawDataRepository.save(rawData);
    }
}
