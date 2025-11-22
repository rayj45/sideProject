package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.Archive;
import jbch.org.sideproject.repository.ArchiveRepository;
import jbch.org.sideproject.request.ArchiveCreate;
import jbch.org.sideproject.request.ArchiveEdit;
import jbch.org.sideproject.response.ArchiveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void write(ArchiveCreate archiveCreate) throws IOException {
        // ... (기존 write 로직과 동일)
    }

    public Page<ArchiveResponse> list(Pageable pageable) {
        return archiveRepository.findAll(pageable).map(ArchiveResponse::new);
    }

    public ArchiveResponse read(Long id) {
        Archive archive = archiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자료입니다."));
        return new ArchiveResponse(archive);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void modify(Long id, ArchiveEdit archiveEdit) throws IOException {
        Archive archive = archiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자료입니다."));

        // 파일 수정 로직 (새 파일이 있으면 기존 파일 삭제 후 업로드)
        MultipartFile newFile = archiveEdit.getFile();
        String originalFileName = archive.getOriginalFileName();
        String storedFilePath = archive.getStoredFilePath();

        if (newFile != null && !newFile.isEmpty()) {
            // 기존 파일 삭제
            if (storedFilePath != null) {
                Files.deleteIfExists(Paths.get(storedFilePath));
            }
            // 새 파일 업로드
            originalFileName = newFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = uuid + extension;
            storedFilePath = uploadDir + File.separator + fileName;
            newFile.transferTo(new File(storedFilePath));
        }

        archive.modify(archiveEdit.getTitle(), archiveEdit.getContent(), originalFileName, storedFilePath);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) throws IOException {
        Archive archive = archiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자료입니다."));

        // 첨부파일이 있으면 물리적 파일 삭제
        if (archive.getStoredFilePath() != null) {
            Files.deleteIfExists(Paths.get(archive.getStoredFilePath()));
        }

        archiveRepository.delete(archive);
    }

    public Archive getArchiveEntity(Long id) {
        return archiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자료입니다."));
    }
}
