package jbch.org.sideproject.service;

import jbch.org.sideproject.domain.Archive;
import jbch.org.sideproject.repository.ArchiveRepository;
import jbch.org.sideproject.request.ArchiveCreate;
import jbch.org.sideproject.response.ArchiveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public void write(ArchiveCreate archiveCreate) throws IOException {
        MultipartFile file = archiveCreate.getFile();
        String originalFileName = null;
        String storedFilePath = null;

        if (file != null && !file.isEmpty()) {
            originalFileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = uuid + extension;
            storedFilePath = uploadDir + File.separator + fileName;

            File dest = new File(storedFilePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
        }

        Archive archive = Archive.builder()
                .title(archiveCreate.getTitle())
                .content(archiveCreate.getContent())
                .originalFileName(originalFileName)
                .storedFilePath(storedFilePath)
                .build();

        archiveRepository.save(archive);
    }

    public Page<ArchiveResponse> list(Pageable pageable) {
        return archiveRepository.findAll(pageable).map(ArchiveResponse::new);
    }

    public ArchiveResponse read(Long id) {
        Archive archive = archiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자료입니다."));
        return new ArchiveResponse(archive);
    }

    public Archive getArchiveEntity(Long id) {
        return archiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자료입니다."));
    }

    // TODO: modify, delete 기능 추가 예정
}
