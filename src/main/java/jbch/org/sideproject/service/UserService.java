package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.domain.UserRole;
import jbch.org.sideproject.domain.UserStatus;
import jbch.org.sideproject.repository.UserRepository;
import jbch.org.sideproject.request.UserEditRequestDto;
import jbch.org.sideproject.request.UserSignupRequestDto;
import jbch.org.sideproject.response.MyPageResponseDto;
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
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .username(requestDto.getUsername())
                .nickName(requestDto.getNickName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .email(requestDto.getEmail())
                .phone(requestDto.getPhone())
                .userGroup(requestDto.getUserGroup())
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .sellerApproved(false)
                .build();

        userRepository.save(user);
    }

    public MyPageResponseDto getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new MyPageResponseDto(user);
    }

    @Transactional
    public String modify(UserEditRequestDto requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 변경 로직 강화
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

        // 나머지 정보 변경
        user.modifyInfo(
                requestDto.getNickName(),
                requestDto.getEmail(),
                requestDto.getPhone(),
                requestDto.getUserGroup()
        );
        
        return null; // 성공 시 null 반환
    }

    @Transactional
    public void delete() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }
}
