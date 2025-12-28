package jbch.org.sideproject.service;

import jakarta.servlet.http.HttpSession;
import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.domain.UserRole;
import jbch.org.sideproject.domain.UserStatus;
import jbch.org.sideproject.repository.UserRepository;
import jbch.org.sideproject.request.UserEditRequestDto;
import jbch.org.sideproject.request.UserSignupRequestDto;
import jbch.org.sideproject.response.MyPageResponseDto;
import jbch.org.sideproject.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final HttpSession httpSession;

    public void signup(UserSignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(requestDto.getEmail())
                .nickName(requestDto.getNickName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .phone(requestDto.getPhone())
                .userGroup(requestDto.getUserGroup())
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .sellerApproved(false)
                .build();

        userRepository.save(user);
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public MyPageResponseDto getMyInfo() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new MyPageResponseDto(user);
    }

    @Transactional
    public String modify(UserEditRequestDto requestDto) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean isPasswordChangeAttempt = StringUtils.hasText(requestDto.getCurrentPassword()) || StringUtils.hasText(requestDto.getNewPassword());

        if (isPasswordChangeAttempt) {
            if (!StringUtils.hasText(requestDto.getCurrentPassword())) {
                return "현재 비밀번호를 입력해주세요.";
            }
            if (!StringUtils.hasText(requestDto.getNewPassword())) {
                return "새 비밀번호를 입력해주세요.";
            }
            if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
                return "현재 비밀번호가 일치하지 않습니다.";
            }
            user.changePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        }

        user.modifyInfo(
                requestDto.getNickName(),
                requestDto.getEmail(),
                requestDto.getPhone(),
                requestDto.getUserGroup()
        );
        
        return null;
    }

    @Transactional
    public void delete() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userRepository.deleteById(userPrincipal.getId());
    }


    public void sendVerificationCode(String email) {
        if (checkEmailDuplicate(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String code = createVerificationCode(); //난수 생성
        emailService.sendVerificationCode(email, code);

        // 세션에 인증번호와 이메일 저장 (유효시간 5분)
        httpSession.setAttribute("verificationCode", code);
        httpSession.setAttribute("verificationEmail", email);
        httpSession.setMaxInactiveInterval(300); // 5분
    }

    public boolean verifyCode(String email, String code) {
        String sessionCode = (String) httpSession.getAttribute("verificationCode");
        String sessionEmail = (String) httpSession.getAttribute("verificationEmail");

        if (sessionCode != null && sessionEmail != null && sessionEmail.equals(email) && sessionCode.equals(code)) {
            httpSession.removeAttribute("verificationCode"); // 인증 성공 시 세션에서 제거
            httpSession.removeAttribute("verificationEmail");
            return true;
        }
        return false;
    }

    public void sendPasswordResetCode(String email) {
        if (!checkEmailDuplicate(email)) {
            throw new IllegalArgumentException("가입되지 않은 이메일입니다.");
        }
        String code = createVerificationCode();
        emailService.sendVerificationCode(email, code); // 기존 메서드 재활용

        httpSession.setAttribute("resetPasswordCode", code);
        httpSession.setAttribute("resetPasswordEmail", email);
        httpSession.setMaxInactiveInterval(300); // 5분
    }

    public boolean verifyPasswordResetCode(String email, String code) {
        String sessionCode = (String) httpSession.getAttribute("resetPasswordCode");
        String sessionEmail = (String) httpSession.getAttribute("resetPasswordEmail");

        if (sessionCode != null && sessionEmail != null && sessionEmail.equals(email) && sessionCode.equals(code)) {
            httpSession.setAttribute("passwordResetVerified", true); // 인증 완료 상태 저장
            httpSession.setAttribute("passwordResetEmail", email); // 이메일 정보 유지
            return true;
        }
        return false;
    }

    @Transactional
    public void resetPassword(String newPassword) {
        Boolean isVerified = (Boolean) httpSession.getAttribute("passwordResetVerified");
        String email = (String) httpSession.getAttribute("passwordResetEmail");

        if (isVerified == null || !isVerified || email == null) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        user.changePassword(passwordEncoder.encode(newPassword));

        // 비밀번호 재설정 후 세션 정보 정리
        httpSession.removeAttribute("passwordResetVerified");
        httpSession.removeAttribute("passwordResetEmail");
        httpSession.removeAttribute("resetPasswordCode");
    }

    private String createVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 난수 생성
        return String.valueOf(code);
    }
}
