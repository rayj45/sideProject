package jbch.org.sideproject.controller;

import jbch.org.sideproject.request.UserSignupRequestDto;
import jbch.org.sideproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute UserSignupRequestDto requestDto) {
        userService.signup(requestDto);
        return "redirect:/login"; // 회원가입 성공 시 로그인 페이지로
    }
}
