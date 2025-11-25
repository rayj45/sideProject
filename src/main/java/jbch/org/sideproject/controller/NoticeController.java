package jbch.org.sideproject.controller;

import jbch.org.sideproject.request.PostCreate;
import jbch.org.sideproject.request.PostEdit;
import jbch.org.sideproject.response.PostResponse;
import jbch.org.sideproject.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final PostService postService;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponse> posts = postService.list(pageable);
        model.addAttribute("posts", posts);
        return "notice/list";
    }

    @GetMapping("/write")
    public String write() {
        return "notice/write";
    }

    @PostMapping("/write")
    public String write(@ModelAttribute PostCreate request) {
        postService.write(request);
        return "redirect:/notice/list";
    }

    @GetMapping("/read/{postId}")
    public String read(@PathVariable Long postId, Model model) {
        PostResponse post = postService.read(postId);
        model.addAttribute("post", post);
        return "notice/read";
    }

    @GetMapping("/modify/{postId}")
    public String modify(@PathVariable Long postId, Model model) {
        PostResponse post = postService.read(postId);
        model.addAttribute("post", post);
        return "notice/modify";
    }

    @PostMapping("/modify/{postId}")
    public String modify(@PathVariable Long postId, @ModelAttribute PostEdit postEdit) {
        postService.modify(postId, postEdit);
        return "redirect:/notice/read/" + postId;
    }

    @PostMapping("/delete/{postId}")
    public String delete(@PathVariable Long postId) {
        postService.delete(postId);
        return "redirect:/notice/list";
    }
}
