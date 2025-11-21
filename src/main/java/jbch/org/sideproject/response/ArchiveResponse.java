package jbch.org.sideproject.response;

import jbch.org.sideproject.domain.Archive;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class ArchiveResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String originalFileName;
    private final String createdDate;

    public ArchiveResponse(Archive archive) {
        this.id = archive.getId();
        this.title = archive.getTitle();
        this.content = archive.getContent();
        this.originalFileName = archive.getOriginalFileName();
        this.createdDate = archive.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
