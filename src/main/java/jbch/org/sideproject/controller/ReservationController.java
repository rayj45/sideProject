package jbch.org.sideproject.controller;

import jbch.org.sideproject.request.ReservationRequestDto;
import jbch.org.sideproject.response.ReservationStatusDto;
import jbch.org.sideproject.response.admin.RoomAdminResponseDto;
import jbch.org.sideproject.service.ReservationService;
import jbch.org.sideproject.service.RoomService; // RoomService 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final RoomService roomService; // RoomAdminService 대신 RoomService 주입
    private final ReservationService reservationService;

    @GetMapping("/list")
    public String roomList(Model model, @PageableDefault(size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RoomAdminResponseDto> rooms = roomService.list(pageable); // RoomService 사용
        model.addAttribute("rooms", rooms);
        return "reservation/list";
    }

    @GetMapping("/read/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model) {
        RoomAdminResponseDto room = roomService.read(roomId); // RoomService 사용
        model.addAttribute("room", room);
        return "reservation/read";
    }

    @GetMapping("/apply/{roomId}")
    public String applyPage(@PathVariable Long roomId,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            Model model) {
        
        LocalDate today = LocalDate.now();
        LocalDate selectedDate = (date == null) ? today : date;

        RoomAdminResponseDto room = roomService.read(roomId); // RoomService 사용
        List<ReservationStatusDto> reservationStatus = reservationService.getReservationStatus(roomId, selectedDate);

        model.addAttribute("room", room);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("timeSlots", reservationService.generateTimeSlots());
        model.addAttribute("reservationStatus", reservationStatus);
        
        return "reservation/apply";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createReservation(@RequestBody ReservationRequestDto requestDto) {
        try {
            reservationService.createReservation(requestDto);
            return ResponseEntity.ok(Map.of("message", "예약 신청이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
