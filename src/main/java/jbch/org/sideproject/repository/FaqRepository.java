package jbch.org.sideproject.repository;

import jbch.org.sideproject.domain.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findAllByOrderByCreatedDateDesc();
}
