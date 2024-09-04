package ru.vsu.cs.services.impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vsu.cs.entities.AppDocument;
import ru.vsu.cs.entities.BinaryContent;
import ru.vsu.cs.exceptions.UploadFileException;
import ru.vsu.cs.repositories.AppDocumentRepository;
import ru.vsu.cs.repositories.BinaryContentRepository;
import ru.vsu.cs.services.FileService;

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
    private final AppDocumentRepository appDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;

    public FileServiceImpl(AppDocumentRepository appDocumentRepository, BinaryContentRepository binaryContentRepository) {
        this.appDocumentRepository = appDocumentRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonObject = new JSONObject(response.getBody());
            String filePath = String.valueOf(jsonObject
                    .getJSONObject("result")
                    .getString("file_path"));
            byte[] fileInByte = downloadFile(filePath);
            BinaryContent transientBinaryContent = BinaryContent
                    .builder()
                    .fileAsArrayOfBytes(fileInByte)
                    .build();
            BinaryContent persistentBinaryContent = binaryContentRepository.save(transientBinaryContent);
            Document telegramDoc = telegramMessage.getDocument();
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return transientAppDoc;
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
    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request  = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
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

        try (InputStream inputStream = urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }
}
