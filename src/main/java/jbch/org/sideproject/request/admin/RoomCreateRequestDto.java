package jbch.org.sideproject.request.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class RoomCreateRequestDto {
    private String name;
    private int capacity;
    private String description;
    private List<MultipartFile> images;
}
