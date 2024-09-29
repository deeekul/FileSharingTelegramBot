package ru.vsu.cs.services;

import ru.vsu.cs.dto.MailParams;

public interface ConsumerService {

    void consumeRegistrationMail(MailParams mailParams);
}
