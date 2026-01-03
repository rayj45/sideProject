package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.Room;
import jbch.org.sideproject.repository.RoomRepository;
import jbch.org.sideproject.response.admin.RoomAdminResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public Page<RoomAdminResponseDto> list(Pageable pageable) {
        return roomRepository.findAll(pageable).map(RoomAdminResponseDto::new);
    }

    @Transactional(readOnly = true)
    public RoomAdminResponseDto read(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("부속실을 찾을 수 없습니다."));
        return new RoomAdminResponseDto(room);
    }
}
