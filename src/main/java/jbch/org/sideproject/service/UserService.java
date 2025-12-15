package jbch.org.sideproject.service;

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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
