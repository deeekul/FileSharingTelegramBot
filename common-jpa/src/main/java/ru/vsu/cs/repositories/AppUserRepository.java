package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.entities.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByTelegramUserId(Long id);
}
