package jbch.org.sideproject.repository;

import jbch.org.sideproject.domain.Archive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
}
