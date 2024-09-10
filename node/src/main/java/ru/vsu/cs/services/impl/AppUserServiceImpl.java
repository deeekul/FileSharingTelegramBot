package ru.vsu.cs.services.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.vsu.cs.dto.MailParams;
import ru.vsu.cs.entities.AppUser;
import ru.vsu.cs.repositories.AppUserRepository;
import ru.vsu.cs.services.AppUserService;
import ru.vsu.cs.utils.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static ru.vsu.cs.enums.UserState.BASIC_STATE;
import static ru.vsu.cs.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserRepository appUserRepository, CryptoTool cryptoTool) {
        this.appUserRepository = appUserRepository;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.isActive()) {
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) {
            return "Вам на почту уже было отправлено письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации!";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE);
        appUserRepository.save(appUser);
        return "Пожалуйста, введите Ваш email:";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Пожалуйста, введите корректный email. Для отмена команды нажмите /cancel";
        }
        var foundUser = appUserRepository.findByEmail(email);
        if (foundUser.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(BASIC_STATE);
            appUserRepository.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            var response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                var message = String.format("Отправка электронного письма на почту %s не удалась", email);
                log.error(message);
                appUser.setEmail(null);
                appUserRepository.save(appUser);
                return message;
            }
            return "Вам на почту было отправлено письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
            return "Этот email уже используется. Введите корректный email."
                    + "Для отмены команды ведите /cancel";
        }

    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        var request = new HttpEntity<>(mailParams, headers);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}
