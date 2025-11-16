package jbch.org.sideproject.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PostCreate {

    private String title;
    private String content;

    public PostCreate() {
    }

    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
