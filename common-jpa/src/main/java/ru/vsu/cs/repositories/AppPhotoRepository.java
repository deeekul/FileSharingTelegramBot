package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.entities.AppPhoto;

@Repository
public interface AppPhotoRepository extends JpaRepository<AppPhoto, Long> {
}
