package jbch.org.sideproject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;

    private String storedFilePath;

    private boolean isThumbnail;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder
    public RoomImage(String originalFileName, String storedFilePath, boolean isThumbnail) {
        this.originalFileName = originalFileName;
        this.storedFilePath = storedFilePath;
        this.isThumbnail = isThumbnail;
    }
}
