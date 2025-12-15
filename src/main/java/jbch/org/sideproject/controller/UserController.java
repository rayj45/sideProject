package jbch.org.sideproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbch.org.sideproject.request.UserEditRequestDto;
import jbch.org.sideproject.request.UserSignupRequestDto;
import jbch.org.sideproject.response.MyPageResponseDto;
import jbch.org.sideproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupPage(){
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute UserSignupRequestDto requestDto){
        userService.signup(requestDto);
        return "redirect:/login";
    }

    @PostMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isDuplicate = userService.checkEmailDuplicate(email);
        return Map.of("isDuplicate", isDuplicate);
    }

    @GetMapping("/mypage")
    public String mypage(Model model){
        MyPageResponseDto myInfo = userService.getMyInfo();
        model.addAttribute("user", myInfo);
        return "mypage";
    }

    @GetMapping("/edit")
    public String editPage(Model model) {
        if (!model.containsAttribute("user")) {
            MyPageResponseDto myInfo = userService.getMyInfo();
            model.addAttribute("user", myInfo);
        }
        return "edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute UserEditRequestDto requestDto, RedirectAttributes redirectAttributes) {
        String errorMessage = userService.modify(requestDto);

        if (errorMessage != null) {
            redirectAttributes.addFlashAttribute("user", requestDto);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/user/edit";
        }

        return "redirect:/user/mypage";
    }

    @PostMapping("/delete")
    public String delete(HttpServletRequest request, HttpServletResponse response) {
        userService.delete();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }
}
