package ru.vsu.cs.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
