package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.entities.AppDocument;

@Repository
public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {
}
