package jbch.org.sideproject.response;

import jbch.org.sideproject.domain.Reservation;
import jbch.org.sideproject.domain.ReservationStatus;
import lombok.Getter;

@Getter
public class ReservationStatusDto {
    private final String time; // "09:00", "09:30" ...
    private final ReservationStatus status;
    private final String reservedBy;
    private final String reason; // 예약 사유 추가

    public ReservationStatusDto(String time, Reservation reservation) {
        this.time = time;
        if (reservation != null) {
            this.status = reservation.getStatus();
            this.reservedBy = reservation.getUser().getNickName();
            this.reason = reservation.getReason();
        } else {
            this.status = null; // 예약 없음
            this.reservedBy = null;
            this.reason = null;
        }
    }
}
