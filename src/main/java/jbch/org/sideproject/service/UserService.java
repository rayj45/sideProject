package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.domain.UserRole;
import jbch.org.sideproject.domain.UserStatus;
import jbch.org.sideproject.repository.UserRepository;
import jbch.org.sideproject.request.UserSignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .email(requestDto.getEmail())
                .phone(requestDto.getPhone())
                .userGroup(requestDto.getUserGroup())
                .role(UserRole.ROLE_USER) // 일반 사용자로 가입
                .status(UserStatus.ACTIVE)
                .sellerApproved(false)
                .build();

        userRepository.save(user);
    }
}
