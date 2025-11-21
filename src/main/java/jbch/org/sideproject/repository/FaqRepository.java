package jbch.org.sideproject.repository;

import jbch.org.sideproject.domain.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {
}
