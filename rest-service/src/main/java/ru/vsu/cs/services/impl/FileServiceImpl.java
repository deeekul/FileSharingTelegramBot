package ru.vsu.cs.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.AppPhoto;
import ru.vsu.cs.repositories.AppDocumentRepository;
import ru.vsu.cs.repositories.AppPhotoRepository;
import ru.vsu.cs.services.FileService;
import ru.vsu.cs.utils.CryptoTool;

@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentRepository appDocumentRepository;

    private final AppPhotoRepository appPhotoRepository;

    private final CryptoTool cryptoTool;

    @Override
    public AppDocument getDocument(String docId) {
        var id = cryptoTool.idOf(docId);
        return appDocumentRepository.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        var id = cryptoTool.idOf(photoId);
        return appPhotoRepository.findById(id).orElse(null);
    }
}
