package ru.vsu.cs.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vsu.cs.entities.AppDocument;

public interface FileService {
    AppDocument processDoc(Message externalMessage);
}
