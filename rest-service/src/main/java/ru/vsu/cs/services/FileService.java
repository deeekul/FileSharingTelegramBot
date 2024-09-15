package ru.vsu.cs.services;

import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.AppPhoto;

public interface FileService {

    AppDocument getDocument(String docId);

    AppPhoto getPhoto(String photoId);
}
