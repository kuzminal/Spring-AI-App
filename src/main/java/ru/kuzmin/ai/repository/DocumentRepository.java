package ru.kuzmin.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kuzmin.ai.model.LoadedDocument;

public interface DocumentRepository extends JpaRepository<LoadedDocument, Long> {
    boolean existsByFilenameAndContentHash(String filename, String contentHash);
}
