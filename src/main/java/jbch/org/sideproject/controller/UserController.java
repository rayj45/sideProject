package jbch.org.sideproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbch.org.sideproject.request.UserEditRequestDto;
import jbch.org.sideproject.request.UserSignupRequestDto;
import jbch.org.sideproject.response.MyPageResponseDto;
import jbch.org.sideproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public String signupPage(Model model){
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(HttpServletRequest request) {
        UserSignupRequestDto requestDto = new UserSignupRequestDto();
        requestDto.setEmail(request.getParameter("email"));
        requestDto.setNickName(request.getParameter("nickName"));
        requestDto.setPassword(request.getParameter("password"));
        requestDto.setPhone(request.getParameter("phone"));
        requestDto.setUserGroup(request.getParameter("userGroup"));
        
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
    public String edit(@ModelAttribute("user") UserEditRequestDto requestDto, RedirectAttributes redirectAttributes) {
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

    @PostMapping("/send-verification")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendVerificationCode(@RequestBody Map<String, String> request) {
        try {
            userService.sendVerificationCode(request.get("email"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/verify-code")
    @ResponseBody
    public Map<String, String> verifyCode(@RequestBody Map<String, String> request) {
        UserService.VerificationResult result = userService.verifyCode(request.get("email"), request.get("code"));
        return Map.of("status", result.name());
    }

    @GetMapping("/find-password")
    public String findPasswordPage() {
        return "find-password";
    }

    @PostMapping("/send-password-reset-code")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendPasswordResetCode(@RequestBody Map<String, String> request) {
        try {
            userService.sendPasswordResetCode(request.get("email"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/verify-password-reset-code")
    @ResponseBody
    public Map<String, String> verifyPasswordResetCode(@RequestBody Map<String, String> request) {
        UserService.VerificationResult result = userService.verifyPasswordResetCode(request.get("email"), request.get("code"));
        return Map.of("status", result.name());
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            userService.resetPassword(newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/find-password";
        }
    }
}
