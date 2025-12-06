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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            // 실패 시, username을 DTO에 설정하여 리다이렉트
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            requestDto.setUsername(username);
            
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
