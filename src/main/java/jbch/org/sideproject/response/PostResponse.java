package jbch.org.sideproject.response;

import jbch.org.sideproject.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String createdDate;
    private final String modifiedDate;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.modifiedDate = post.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Builder
    public PostResponse(Long id, String title, String content, String createdDate, String modifiedDate) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10));
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
