package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.Faq;
import jbch.org.sideproject.repository.FaqRepository;
import jbch.org.sideproject.request.FaqCreate;
import jbch.org.sideproject.request.FaqEdit;
import jbch.org.sideproject.response.FaqResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    public List<FaqResponse> list() {
        return faqRepository.findAllByOrderByCreatedDateDesc().stream()
                .map(FaqResponse::new)
                .collect(Collectors.toList());
    }

    public FaqResponse read(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 FAQ입니다."));
        return new FaqResponse(faq);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void write(FaqCreate faqCreate) {
        Faq faq = Faq.builder()
                .question(faqCreate.getQuestion())
                .answer(faqCreate.getAnswer())
                .build();
        faqRepository.save(faq);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void modify(Long id, FaqEdit faqEdit) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 FAQ입니다."));
        faq.modify(faqEdit.getQuestion(), faqEdit.getAnswer());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        faqRepository.deleteById(id);
    }
}
