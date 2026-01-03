package jbch.org.sideproject.request.admin;

import jbch.org.sideproject.domain.RoomStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class RoomAdminEditRequestDto {
    private String name;
    private int capacity;
    private String description;
    private RoomStatus status;
    private List<MultipartFile> newImages;
    private List<Long> deleteImageIds;
}
