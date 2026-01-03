package jbch.org.sideproject.response.admin;

import jbch.org.sideproject.domain.Room;
import jbch.org.sideproject.domain.RoomStatus;
import lombok.Getter;

@Getter
public class RoomAdminListDto {
    private final Long id;
    private final String name;
    private final int capacity;
    private final RoomStatus status;
    private final String thumbnailUrl;

    public RoomAdminListDto(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.capacity = room.getCapacity();
        this.status = room.getStatus();
        this.thumbnailUrl = room.getImages().stream()
                .filter(RoomImage -> RoomImage.isThumbnail())
                .findFirst()
                .map(RoomImage -> "/images/" + RoomImage.getStoredFilePath().substring(RoomImage.getStoredFilePath().lastIndexOf("/") + 1))
                .orElse("/images/default-thumbnail.png"); // 썸네일이 없을 경우 기본 이미지
    }
}
