package jbch.org.sideproject.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequestDto {
    private Long roomId;
    private String startTime; // "yyyy-MM-ddTHH:mm"
    private String endTime;   // "yyyy-MM-ddTHH:mm"
    private String reason;    // 예약 사유 추가
}
