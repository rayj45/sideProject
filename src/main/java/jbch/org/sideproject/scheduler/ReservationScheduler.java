package jbch.org.sideproject.scheduler;

import jbch.org.sideproject.domain.Reservation;
import jbch.org.sideproject.domain.ReservationStatus;
import jbch.org.sideproject.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    // 1분마다 실행
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void completeExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndReservationEndTimeBefore(ReservationStatus.APPROVED, now);

        for (Reservation reservation : expiredReservations) {
            reservation.updateStatus(ReservationStatus.COMPLETED);
        }
    }
}
