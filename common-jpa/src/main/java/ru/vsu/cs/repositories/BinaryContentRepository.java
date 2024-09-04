package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.entities.BinaryContent;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {
}
