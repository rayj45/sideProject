package jbch.org.sideproject.service.admin;

import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.repository.UserRepository;
import jbch.org.sideproject.request.UserAdminEditRequestDto;
import jbch.org.sideproject.response.UserAdminResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminService {

    private final UserRepository userRepository;

    public Page<UserAdminResponseDto> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserAdminResponseDto::new);
    }

    public UserAdminResponseDto read(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserAdminResponseDto(user);
    }

    @Transactional
    public void modify(Long userId, UserAdminEditRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        user.adminModifyInfo(
                requestDto.getNickName(),
                requestDto.getPhone(),
                requestDto.getUserGroup(),
                requestDto.getRole(),
                requestDto.getStatus(),
                requestDto.getSellerApproved()
        );
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
