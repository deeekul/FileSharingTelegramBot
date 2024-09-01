package ru.vsu.cs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.entities.RawData;

public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
