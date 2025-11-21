package jbch.org.sideproject.response;

import jbch.org.sideproject.domain.Faq;
import lombok.Getter;

@Getter
public class FaqResponse {

    private final Long id;
    private final String question;
    private final String answer;

    public FaqResponse(Faq faq) {
        this.id = faq.getId();
        this.question = faq.getQuestion();
        this.answer = faq.getAnswer();
    }
}
