package jbch.org.sideproject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int capacity;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Lob
    private String description;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImage> images = new ArrayList<>();

    @Builder
    public Room(String name, int capacity, RoomStatus status, String description) {
        this.name = name;
        this.capacity = capacity;
        this.status = status;
        this.description = description;
    }

    public void addImage(RoomImage image) {
        this.images.add(image);
        image.setRoom(this);
    }

    public void adminModifyInfo(String name, int capacity, String description, RoomStatus status) {
        this.name = name;
        this.capacity = capacity;
        this.description = description;
        this.status = status;
    }
}
