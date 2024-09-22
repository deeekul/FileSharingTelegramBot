package ru.vsu.cs.services;

import ru.vsu.cs.dto.MailParams;

public interface MailSenderService {

    void send(MailParams mailParams);
}
