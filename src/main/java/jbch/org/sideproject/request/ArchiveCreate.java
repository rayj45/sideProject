package jbch.org.sideproject.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ArchiveCreate {
    private String title;
    private String content;
    private MultipartFile file;
}
