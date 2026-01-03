package jbch.org.sideproject.response.admin;

import jbch.org.sideproject.domain.Room;
import jbch.org.sideproject.domain.RoomImage;
import jbch.org.sideproject.domain.RoomStatus;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RoomAdminResponseDto {
    private final Long id;
    private final String name;
    private final int capacity;
    private final RoomStatus status;
    private final String description;
    private final String thumbnailPath;
    private final List<ImageInfo> images; // 이미지 정보를 담는 내부 클래스 리스트

    @Getter
    public static class ImageInfo {
        private final Long id;
        private final String path;

        public ImageInfo(RoomImage image) {
            this.id = image.getId();
            this.path = image.getStoredFilePath();
        }
    }

    public RoomAdminResponseDto(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.capacity = room.getCapacity();
        this.status = room.getStatus();
        this.description = room.getDescription();
        
        this.thumbnailPath = room.getImages().stream()
                .filter(RoomImage::isThumbnail)
                .findFirst()
                .map(RoomImage::getStoredFilePath)
                .orElse(null);

        this.images = room.getImages().stream()
                .map(ImageInfo::new)
                .collect(Collectors.toList());
    }
}
