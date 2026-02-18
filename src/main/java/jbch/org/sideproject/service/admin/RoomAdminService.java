package jbch.org.sideproject.service.admin;

import jbch.org.sideproject.domain.Room;
import jbch.org.sideproject.domain.RoomImage;
import jbch.org.sideproject.domain.RoomStatus;
import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.repository.RoomImageRepository;
import jbch.org.sideproject.repository.RoomRepository;
import jbch.org.sideproject.repository.UserRepository;
import jbch.org.sideproject.request.admin.RoomCreateRequestDto;
import jbch.org.sideproject.request.admin.RoomAdminEditRequestDto;
import jbch.org.sideproject.response.admin.RoomAdminResponseDto;
import jbch.org.sideproject.security.UserPrincipal;
import jbch.org.sideproject.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoomAdminService {

    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional
    public void createRoom(RoomCreateRequestDto requestDto) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User admin = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));

        Room room = Room.builder()
                .name(requestDto.getName())
                .capacity(requestDto.getCapacity())
                .description(requestDto.getDescription())
                .status(RoomStatus.AVAILABLE)
                .roomGroup(admin.getUserGroup()) // 관리자의 그룹 정보 저장
                .build();

        boolean isFirstImage = true;
        if (requestDto.getImages() != null && !requestDto.getImages().isEmpty()) {
            for (MultipartFile file : requestDto.getImages()) {
                if(file.isEmpty()) continue;

                String storedFilePath = fileService.storeFile(file);
                RoomImage roomImage = RoomImage.builder()
                        .originalFileName(file.getOriginalFilename())
                        .storedFilePath(storedFilePath)
                        .isThumbnail(isFirstImage)
                        .build();
                room.addImage(roomImage);
                isFirstImage = false;
            }
        }
        roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Page<RoomAdminResponseDto> list(Pageable pageable, String searchGroup) {
        if (searchGroup != null && !searchGroup.isEmpty()) {
            return roomRepository.findByRoomGroupContaining(searchGroup, pageable).map(RoomAdminResponseDto::new);
        }
        return roomRepository.findAll(pageable).map(RoomAdminResponseDto::new);
    }

    @Transactional(readOnly = true)
    public RoomAdminResponseDto read(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("부속실을 찾을 수 없습니다."));
        return new RoomAdminResponseDto(room);
    }

    @Transactional
    public void modify(Long roomId, RoomAdminEditRequestDto requestDto) throws IOException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("부속실을 찾을 수 없습니다."));

        room.adminModifyInfo(
                requestDto.getName(),
                requestDto.getCapacity(),
                requestDto.getDescription(),
                requestDto.getStatus(),
                room.getRoomGroup() // 그룹은 변경하지 않음 (기존 값 유지)
        );

        // 이미지 삭제
        if (requestDto.getDeleteImageIds() != null) {
            for (Long imageId : requestDto.getDeleteImageIds()) {
                RoomImage image = roomImageRepository.findById(imageId)
                        .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));
                fileService.deleteFile(image.getStoredFilePath());
                room.getImages().remove(image);
                roomImageRepository.delete(image);
            }
        }

        // 새 이미지 추가
        if (requestDto.getNewImages() != null && !requestDto.getNewImages().isEmpty()) {
            boolean isThumbnailNeeded = room.getImages().stream().noneMatch(RoomImage::isThumbnail);
            for (MultipartFile file : requestDto.getNewImages()) {
                if(file.isEmpty()) continue;
                String storedFilePath = fileService.storeFile(file);
                RoomImage roomImage = RoomImage.builder()
                        .originalFileName(file.getOriginalFilename())
                        .storedFilePath(storedFilePath)
                        .isThumbnail(isThumbnailNeeded)
                        .build();
                room.addImage(roomImage);
                isThumbnailNeeded = false;
            }
        }
    }

    @Transactional
    public void delete(Long roomId) throws IOException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("부속실을 찾을 수 없습니다."));
        
        for (RoomImage image : room.getImages()) {
            fileService.deleteFile(image.getStoredFilePath());
        }
        
        roomRepository.delete(room);
    }
}
