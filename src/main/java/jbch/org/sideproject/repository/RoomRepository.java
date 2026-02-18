package jbch.org.sideproject.repository;

import jbch.org.sideproject.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findByRoomGroup(String roomGroup, Pageable pageable);
    Page<Room> findByRoomGroupContaining(String roomGroup, Pageable pageable);
}
