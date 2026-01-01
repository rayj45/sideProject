package jbch.org.sideproject.controller.admin;

import jbch.org.sideproject.request.UserAdminEditRequestDto;
import jbch.org.sideproject.response.UserAdminResponseDto;
import jbch.org.sideproject.service.admin.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserAdminResponseDto> users = userAdminService.list(pageable);
        model.addAttribute("users", users);
        return "admin/user/list";
    }

    @GetMapping("/read/{userId}")
    public String read(@PathVariable Long userId, Model model) {
        UserAdminResponseDto user = userAdminService.read(userId);
        model.addAttribute("user", user);
        return "admin/user/read";
    }

    @GetMapping("/modify/{userId}")
    public String modifyForm(@PathVariable Long userId, Model model) {
        UserAdminResponseDto user = userAdminService.read(userId);
        model.addAttribute("user", user);
        return "admin/user/modify";
    }

    @PostMapping("/modify/{userId}")
    public String modify(@PathVariable Long userId, @ModelAttribute UserAdminEditRequestDto requestDto) {
        userAdminService.modify(userId, requestDto);
        return "redirect:/admin/user/read/" + userId;
    }

    @PostMapping("/delete/{userId}")
    public String delete(@PathVariable Long userId) {
        userAdminService.delete(userId);
        return "redirect:/admin/user/list";
    }
}
