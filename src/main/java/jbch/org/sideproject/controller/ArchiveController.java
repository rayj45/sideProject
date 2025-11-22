package jbch.org.sideproject.controller;

import jbch.org.sideproject.domain.Archive;
import jbch.org.sideproject.request.ArchiveCreate;
import jbch.org.sideproject.request.ArchiveEdit;
import jbch.org.sideproject.response.ArchiveResponse;
import jbch.org.sideproject.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/archive")
public class ArchiveController {

    private final ArchiveService archiveService;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<ArchiveResponse> archives = archiveService.list(pageable);
        model.addAttribute("archives", archives);
        return "archive/list";
    }

    @GetMapping("/write")
    public String writeForm() {
        return "archive/write";
    }

    @PostMapping("/write")
    public String write(@ModelAttribute ArchiveCreate request) throws IOException {
        archiveService.write(request);
        return "redirect:/archive/list";
    }

    @GetMapping("/read/{archiveId}")
    public String read(@PathVariable Long archiveId, Model model) {
        ArchiveResponse archive = archiveService.read(archiveId);
        model.addAttribute("archive", archive);
        return "archive/read";
    }

    @GetMapping("/download/{archiveId}")
    public ResponseEntity<Resource> download(@PathVariable Long archiveId) throws MalformedURLException {
        Archive archive = archiveService.getArchiveEntity(archiveId);
        UrlResource resource = new UrlResource("file:" + archive.getStoredFilePath());
        String encodedFileName = UriUtils.encode(archive.getOriginalFileName(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @GetMapping("/modify/{archiveId}")
    public String modifyForm(@PathVariable Long archiveId, Model model) {
        ArchiveResponse archive = archiveService.read(archiveId);
        model.addAttribute("archive", archive);
        return "archive/modify";
    }

    @PostMapping("/modify/{archiveId}")
    public String modify(@PathVariable Long archiveId, @ModelAttribute ArchiveEdit archiveEdit) throws IOException {
        archiveService.modify(archiveId, archiveEdit);
        return "redirect:/archive/read/" + archiveId;
    }

    @PostMapping("/delete/{archiveId}")
    public String delete(@PathVariable Long archiveId) throws IOException {
        archiveService.delete(archiveId);
        return "redirect:/archive/list";
    }
}
