package ru.vsu.cs.services.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.AppPhoto;
import ru.vsu.cs.entities.BinaryContent;
import ru.vsu.cs.repositories.AppDocumentRepository;
import ru.vsu.cs.repositories.AppPhotoRepository;
import ru.vsu.cs.services.FileService;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    private final AppDocumentRepository appDocumentRepository;
    private final AppPhotoRepository appPhotoRepository;

    public FileServiceImpl(AppDocumentRepository appDocumentRepository, AppPhotoRepository appPhotoRepository) {
        this.appDocumentRepository = appDocumentRepository;
        this.appPhotoRepository = appPhotoRepository;
    }

    @Override
    public AppDocument getDocument(String docId) {
        // TODO добавить дешифрование хеш-строки
        var id = Long.parseLong(docId);
        return appDocumentRepository.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        // TODO добавить дешифрование хеш-строки
        var id = Long.parseLong(photoId);
        return appPhotoRepository.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            // TODO добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}
