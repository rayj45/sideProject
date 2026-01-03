package jbch.org.sideproject.response.admin;

import jbch.org.sideproject.domain.Room;
import jbch.org.sideproject.domain.RoomStatus;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RoomAdminDetailDto {
    private final Long id;
    private final String name;
    private final int capacity;
    private final RoomStatus status;
    private final String description;
    private final List<ImageDto> images;

    public RoomAdminDetailDto(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.capacity = room.getCapacity();
        this.status = room.getStatus();
        this.description = room.getDescription();
        this.images = room.getImages().stream()
                .map(ImageDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    private static class ImageDto {
        private final Long id;
        private final String url;
        private final boolean isThumbnail;

        public ImageDto(jbch.org.sideproject.domain.RoomImage image) {
            this.id = image.getId();
            this.url = "/images/" + image.getStoredFilePath().substring(image.getStoredFilePath().lastIndexOf("/") + 1);
            this.isThumbnail = image.isThumbnail();
        }
    }
}
