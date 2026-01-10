package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.*;
import jbch.org.sideproject.repository.ReservationRepository;
import jbch.org.sideproject.repository.RoomRepository;
import jbch.org.sideproject.repository.UserRepository;
import jbch.org.sideproject.request.ReservationRequestDto;
import jbch.org.sideproject.response.ReservationStatusDto;
import jbch.org.sideproject.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<ReservationStatusDto> getReservationStatus(Long roomId, LocalDate date) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("부속실을 찾을 수 없습니다."));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<Reservation> reservations = reservationRepository.findByRoomAndDay(room, startOfDay, endOfDay);
        
        List<ReservationStatusDto> statusList = new ArrayList<>();
        for (String time : generateTimeSlots()) {
            LocalTime localTime = LocalTime.parse(time);
            Reservation reservationForTime = reservations.stream()
                    .filter(r -> !r.getReservationStartTime().toLocalTime().isAfter(localTime) && r.getReservationEndTime().toLocalTime().isAfter(localTime))
                    .findFirst()
                    .orElse(null);
            statusList.add(new ReservationStatusDto(time, reservationForTime));
        }
        return statusList;
    }

    @Transactional
    public void createReservation(ReservationRequestDto requestDto) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Room room = roomRepository.findById(requestDto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("부속실을 찾을 수 없습니다."));

        LocalDateTime reservationStartTime = LocalDateTime.parse(requestDto.getStartTime());
        LocalDateTime reservationEndTime = LocalDateTime.parse(requestDto.getEndTime());

        if (reservationStartTime.isAfter(reservationEndTime) || reservationStartTime.isEqual(reservationEndTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }

        if (reservationRepository.existsOverlappingReservation(room, reservationStartTime, reservationEndTime)) {
            throw new IllegalStateException("선택한 시간에 이미 예약이 존재합니다.");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .room(room)
                .reservationDate(reservationStartTime.toLocalDate())
                .reservationStartTime(reservationStartTime)
                .reservationEndTime(reservationEndTime)
                .status(ReservationStatus.APPROVED)
                .reason(requestDto.getReason()) // 예약 사유 추가
                .build();
        
        reservationRepository.save(reservation);
    }

    public List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                timeSlots.add(String.format("%02d:%02d", hour, minute));
            }
        }
        return timeSlots;
    }
}
