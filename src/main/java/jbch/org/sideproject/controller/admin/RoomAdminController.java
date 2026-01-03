package jbch.org.sideproject.controller.admin;

import jbch.org.sideproject.request.admin.RoomCreateRequestDto;
import jbch.org.sideproject.request.admin.RoomAdminEditRequestDto;
import jbch.org.sideproject.response.admin.RoomAdminResponseDto;
import jbch.org.sideproject.service.admin.RoomAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/room")
public class RoomAdminController {

    private final RoomAdminService roomAdminService;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RoomAdminResponseDto> rooms = roomAdminService.list(pageable);
        model.addAttribute("rooms", rooms);
        return "admin/room/list";
    }

    @GetMapping("/create")
    public String createForm() {
        return "admin/room/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute RoomCreateRequestDto requestDto) throws IOException {
        roomAdminService.createRoom(requestDto);
        return "redirect:/admin/room/list";
    }

    @GetMapping("/read/{roomId}")
    public String read(@PathVariable Long roomId, Model model) {
        RoomAdminResponseDto room = roomAdminService.read(roomId);
        model.addAttribute("room", room);
        return "admin/room/read";
    }

    @GetMapping("/modify/{roomId}")
    public String modifyForm(@PathVariable Long roomId, Model model) {
        RoomAdminResponseDto room = roomAdminService.read(roomId);
        model.addAttribute("room", room);
        return "admin/room/modify";
    }

    @PostMapping("/modify/{roomId}")
    public String modify(@PathVariable Long roomId, @ModelAttribute RoomAdminEditRequestDto requestDto) throws IOException {
        roomAdminService.modify(roomId, requestDto);
        return "redirect:/admin/room/read/" + roomId;
    }

    @PostMapping("/delete/{roomId}")
    public String delete(@PathVariable Long roomId) throws IOException {
        roomAdminService.delete(roomId);
        return "redirect:/admin/room/list";
    }
}
