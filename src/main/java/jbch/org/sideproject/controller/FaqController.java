package jbch.org.sideproject.controller;

import jbch.org.sideproject.request.FaqCreate;
import jbch.org.sideproject.request.FaqEdit;
import jbch.org.sideproject.response.FaqResponse;
import jbch.org.sideproject.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/faq")
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/list")
    public String list(Model model) {
        List<FaqResponse> faqs = faqService.list();
        model.addAttribute("faqs", faqs);
        return "faq/list";
    }

    @GetMapping("/write")
    public String writeForm() {
        return "faq/write";
    }

    @PostMapping("/write")
    public String write(@ModelAttribute FaqCreate faqCreate) {
        faqService.write(faqCreate);
        return "redirect:/faq/list";
    }

    @GetMapping("/modify/{faqId}")
    public String modifyForm(@PathVariable Long faqId, Model model) {
        FaqResponse faq = faqService.read(faqId);
        model.addAttribute("faq", faq);
        return "faq/modify";
    }

    @PostMapping("/modify/{faqId}")
    public String modify(@PathVariable Long faqId, @ModelAttribute FaqEdit faqEdit) {
        faqService.modify(faqId, faqEdit);
        return "redirect:/faq/list";
    }

    @PostMapping("/delete/{faqId}")
    public String delete(@PathVariable Long faqId) {
        faqService.delete(faqId);
        return "redirect:/faq/list";
    }
}
