package jbch.org.sideproject.controller;

import jbch.org.sideproject.request.ArchiveCreate;
import jbch.org.sideproject.response.ArchiveResponse;
import jbch.org.sideproject.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public String write() {
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

    // TODO: modify, delete, download 매핑 추가 예정
}
