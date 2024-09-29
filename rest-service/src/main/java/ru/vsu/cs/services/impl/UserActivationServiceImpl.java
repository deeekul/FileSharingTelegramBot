package ru.vsu.cs.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.vsu.cs.repositories.AppUserRepository;
import ru.vsu.cs.services.UserActivationService;
import ru.vsu.cs.utils.Decoder;

@Log4j
@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserRepository appUserRepository;

    private final Decoder decoder;

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = decoder.idOf(cryptoUserId);
        log.debug(String.format("User activation with user-id=%s", userId));
        if (userId == null) {
            return false;
        }

        var foundUser = appUserRepository.findById(userId);
        if (foundUser.isPresent()) {
            var user = foundUser.get();
            user.setActive(true);
            appUserRepository.save(user);
            return true;
        }
        return false;
    }
}
