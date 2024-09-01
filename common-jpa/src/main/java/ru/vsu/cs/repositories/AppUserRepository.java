package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.entities.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
