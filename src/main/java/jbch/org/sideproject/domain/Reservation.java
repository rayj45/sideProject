package jbch.org.sideproject.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private LocalDateTime reservationStartTime;

    @Column(nullable = false)
    private LocalDateTime reservationEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(length = 500)
    private String reason; // 예약 사유 추가

    @Builder
    public Reservation(User user, Room room, LocalDate reservationDate, LocalDateTime reservationStartTime, LocalDateTime reservationEndTime, ReservationStatus status, String reason) {
        this.user = user;
        this.room = room;
        this.reservationDate = reservationDate;
        this.reservationStartTime = reservationStartTime;
        this.reservationEndTime = reservationEndTime;
        this.status = status;
        this.reason = reason;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}
