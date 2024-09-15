package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.entities.BinaryContent;

@Repository
public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {
}
