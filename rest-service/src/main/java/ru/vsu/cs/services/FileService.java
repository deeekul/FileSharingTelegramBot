package ru.vsu.cs.services;

import org.springframework.core.io.FileSystemResource;
import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.AppPhoto;
import ru.vsu.cs.entities.BinaryContent;

public interface FileService {
    AppDocument getDocument(String docId);
    AppPhoto getPhoto(String photoId);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
