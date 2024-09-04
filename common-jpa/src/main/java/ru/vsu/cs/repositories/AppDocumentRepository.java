package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.entities.AppDocument;

public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {

}
