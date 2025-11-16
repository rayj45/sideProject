package jbch.org.sideproject.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class PostEdit {

    private final String title;
    private final String content;
}
