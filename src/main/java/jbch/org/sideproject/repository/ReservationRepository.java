package jbch.org.sideproject.repository;

import jbch.org.sideproject.domain.Reservation;
import jbch.org.sideproject.domain.ReservationStatus;
import jbch.org.sideproject.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    @Query("SELECT r FROM Reservation r WHERE r.room = :room AND r.reservationStartTime < :endOfDay AND r.reservationEndTime > :startOfDay")
    List<Reservation> findByRoomAndDay(@Param("room") Room room, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.room = :room AND r.status = 'APPROVED' AND r.reservationStartTime < :endTime AND r.reservationEndTime > :startTime")
    boolean existsOverlappingReservation(@Param("room") Room room, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<Reservation> findByStatusAndReservationEndTimeBefore(ReservationStatus status, LocalDateTime now);
}
