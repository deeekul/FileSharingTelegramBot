package ru.vsu.cs.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.dto.MailParams;
import ru.vsu.cs.services.MailSenderService;

@RequestMapping("/mail")
@RestController
public class MailController {
    private final MailSenderService mailSenderService;

    public MailController(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams) {
        mailSenderService.send(mailParams);
        return ResponseEntity.ok().build();
    }
}
