package ru.vsu.cs.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.services.FileService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j
@RequiredArgsConstructor
@RequestMapping("/file")
@RestController
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response) {
        var document = fileService.getDocument(id);
        if (document == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(MediaType.parseMediaType(document.getMimeType()).toString());
        response.setHeader("Content-disposition", "attachment; filename=" + document.getDocName());
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = document.getBinaryContent();
        try {
            var outputStream = response.getOutputStream();
            outputStream.write(binaryContent.getFileAsArrayOfBytes());
            outputStream.close();
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) {
        var photo = fileService.getPhoto(id);
        if (photo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-disposition", "attachment;");
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = photo.getBinaryContent();
        try {
            var outputStream = response.getOutputStream();
            outputStream.write(binaryContent.getFileAsArrayOfBytes());
            outputStream.close();
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
