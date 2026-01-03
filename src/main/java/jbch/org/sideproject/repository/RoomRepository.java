package jbch.org.sideproject.repository;

import jbch.org.sideproject.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
