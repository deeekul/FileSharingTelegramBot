package ru.vsu.cs.services;

import org.springframework.core.io.FileSystemResource;
import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.AppPhoto;
import ru.vsu.cs.entities.BinaryContent;

public interface FileService {
    AppDocument getDocument(String docId);
    AppPhoto getPhoto(String photoId);
    /** To convert an array of bytes from the database into an object of the FileSystemResource class,
     * which can be sent in the response body to the user */
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
