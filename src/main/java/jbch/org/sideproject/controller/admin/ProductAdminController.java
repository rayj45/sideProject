package jbch.org.sideproject.controller.admin;

import jbch.org.sideproject.request.admin.ProductCreateRequestDto;
import jbch.org.sideproject.response.admin.ProductAdminResponseDto;
import jbch.org.sideproject.service.admin.ProductAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/product")
public class ProductAdminController {

    private final ProductAdminService productAdminService;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductAdminResponseDto> products = productAdminService.list(pageable);
        model.addAttribute("products", products);
        return "admin/product/list";
    }

    @GetMapping("/create")
    public String createForm() {
        return "admin/product/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ProductCreateRequestDto requestDto) throws IOException {
        productAdminService.createProduct(requestDto);
        return "redirect:/admin/product/list";
    }

    @GetMapping("/read/{productId}")
    public String read(@PathVariable Long productId, Model model) {
        ProductAdminResponseDto product = productAdminService.read(productId);
        model.addAttribute("product", product);
        return "admin/product/read";
    }

    @PostMapping("/delete/{productId}")
    public String delete(@PathVariable Long productId) throws IOException {
        productAdminService.delete(productId);
        return "redirect:/admin/product/list";
    }
}
