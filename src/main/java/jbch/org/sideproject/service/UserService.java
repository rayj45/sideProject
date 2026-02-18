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

    public enum VerificationResult {
        SUCCESS,
        FAILURE,
        EXPIRED
    }

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
                .status(UserStatus.PENDING) // 가입 시 승인 대기 상태로 설정
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

        String code = createVerificationCode();
        emailService.sendVerificationCode(email, code);

        httpSession.setAttribute("verificationCode", code);
        httpSession.setAttribute("verificationEmail", email);
        httpSession.setMaxInactiveInterval(300);
    }

    public VerificationResult verifyCode(String email, String code) {
        String sessionCode = (String) httpSession.getAttribute("verificationCode");
        String sessionEmail = (String) httpSession.getAttribute("verificationEmail");

        if (sessionCode == null || sessionEmail == null) {
            return VerificationResult.EXPIRED;
        }
        if (sessionEmail.equals(email) && sessionCode.equals(code)) {
            httpSession.removeAttribute("verificationCode");
            httpSession.removeAttribute("verificationEmail");
            return VerificationResult.SUCCESS;
        }
        return VerificationResult.FAILURE;
    }

    public void sendPasswordResetCode(String email) {
        if (!checkEmailDuplicate(email)) {
            throw new IllegalArgumentException("가입되지 않은 이메일입니다.");
        }
        String code = createVerificationCode();
        emailService.sendVerificationCode(email, code);

        httpSession.setAttribute("resetPasswordCode", code);
        httpSession.setAttribute("resetPasswordEmail", email);
        httpSession.setMaxInactiveInterval(300);
    }

    public VerificationResult verifyPasswordResetCode(String email, String code) {
        String sessionCode = (String) httpSession.getAttribute("resetPasswordCode");
        String sessionEmail = (String) httpSession.getAttribute("resetPasswordEmail");

        if (sessionCode == null || sessionEmail == null) {
            return VerificationResult.EXPIRED;
        }
        if (sessionEmail.equals(email) && sessionCode.equals(code)) {
            httpSession.setAttribute("passwordResetVerified", true);
            httpSession.setAttribute("passwordResetEmail", email);
            return VerificationResult.SUCCESS;
        }
        return VerificationResult.FAILURE;
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

        httpSession.removeAttribute("passwordResetVerified");
        httpSession.removeAttribute("passwordResetEmail");
        httpSession.removeAttribute("resetPasswordCode");
    }

    private String createVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
