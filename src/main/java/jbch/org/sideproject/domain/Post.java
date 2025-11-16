package jbch.org.sideproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jbch.org.sideproject.request.PostEdit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void modify(PostEdit postEdit) {
        title = postEdit.getTitle();
        content = postEdit.getContent();
    }
}
