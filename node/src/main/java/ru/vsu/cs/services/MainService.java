package ru.vsu.cs.services;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessages(Update update);
}
