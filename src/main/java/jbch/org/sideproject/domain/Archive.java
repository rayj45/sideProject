package jbch.org.sideproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Archive extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String originalFileName;

    private String storedFilePath;

    @Builder
    public Archive(String title, String content, String originalFileName, String storedFilePath) {
        this.title = title;
        this.content = content;
        this.originalFileName = originalFileName;
        this.storedFilePath = storedFilePath;
    }
}
