package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.entities.AppPhoto;

public interface AppPhotoRepository extends JpaRepository<AppPhoto, Long> {
}
