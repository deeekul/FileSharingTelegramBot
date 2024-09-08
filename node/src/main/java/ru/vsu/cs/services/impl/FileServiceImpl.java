package ru.vsu.cs.services.impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.AppPhoto;
import ru.vsu.cs.entities.BinaryContent;
import ru.vsu.cs.exceptions.UploadFileException;
import ru.vsu.cs.repositories.AppDocumentRepository;
import ru.vsu.cs.repositories.AppPhotoRepository;
import ru.vsu.cs.repositories.BinaryContentRepository;
import ru.vsu.cs.services.FileService;
import ru.vsu.cs.services.enums.LinkType;
import ru.vsu.cs.utils.CryptoTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The service receives a telegram message, performs all necessary actions to download the file and saves it to the database
 */
@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;

    /** The address where we can request information about the file from telegram */
    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    /** The address where you can download the content */
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Value("${link.address}")
    private String linkAddress;
    private final AppDocumentRepository appDocumentRepository;
    private final AppPhotoRepository appPhotoRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentRepository appDocumentRepository, AppPhotoRepository appPhotoRepository, BinaryContentRepository binaryContentRepository, CryptoTool cryptoTool) {
        this.appDocumentRepository = appDocumentRepository;
        this.appPhotoRepository = appPhotoRepository;
        this.binaryContentRepository = binaryContentRepository;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentRepository.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response for telegram service:" + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var photoSizeCount = telegramMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoRepository.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Bad response for telegram service:" + response);
        }
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentRepository.save(transientBinaryContent);
    }

    private static String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token).replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }
}
