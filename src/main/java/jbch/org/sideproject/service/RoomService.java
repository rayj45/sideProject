package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.Room;
import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.repository.RoomRepository;
import jbch.org.sideproject.repository.UserRepository;
import jbch.org.sideproject.response.admin.RoomAdminResponseDto;
import jbch.org.sideproject.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<RoomAdminResponseDto> list(Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (StringUtils.hasText(user.getUserGroup())) {
            return roomRepository.findByRoomGroup(user.getUserGroup(), pageable).map(RoomAdminResponseDto::new);
        }
        
        // 사용자의 그룹 정보가 없으면 빈 페이지 반환
        return Page.empty(pageable);
    }

    @Transactional(readOnly = true)
    public RoomAdminResponseDto read(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("부속실을 찾을 수 없습니다."));
        return new RoomAdminResponseDto(room);
    }
}
