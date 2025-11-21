package jbch.org.sideproject.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaqCreate {
    private String question;
    private String answer;
}
