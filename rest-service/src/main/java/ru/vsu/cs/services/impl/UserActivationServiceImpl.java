package ru.vsu.cs.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.repositories.AppUserRepository;
import ru.vsu.cs.services.UserActivationService;
import ru.vsu.cs.utils.CryptoTool;

@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserRepository appUserRepository;

    private final CryptoTool cryptoTool;

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
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
