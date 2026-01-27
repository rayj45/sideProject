package jbch.org.sideproject.service.admin;

import jbch.org.sideproject.domain.Product;
import jbch.org.sideproject.domain.ProductImage;
import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.repository.ProductImageRepository;
import jbch.org.sideproject.repository.ProductRepository;
import jbch.org.sideproject.repository.UserRepository;
import jbch.org.sideproject.request.admin.ProductCreateRequestDto;
import jbch.org.sideproject.response.admin.ProductAdminResponseDto;
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
@PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
public class ProductAdminService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional
    public void createProduct(ProductCreateRequestDto requestDto) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User seller = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Product product = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .stockQuantity(requestDto.getStockQuantity())
                .description(requestDto.getDescription())
                .category(requestDto.getCategory())
                .seller(seller)
                .build();

        boolean isFirstImage = true;
        if (requestDto.getImages() != null && !requestDto.getImages().isEmpty()) {
            for (MultipartFile file : requestDto.getImages()) {
                if(file.isEmpty()) continue;

                String storedFilePath = fileService.storeFile(file);
                ProductImage productImage = ProductImage.builder()
                        .originalFileName(file.getOriginalFilename())
                        .storedFilePath(storedFilePath)
                        .isThumbnail(isFirstImage)
                        .build();
                product.addImage(productImage);
                isFirstImage = false;
            }
        }
        productRepository.save(product);
    }

    public Page<ProductAdminResponseDto> list(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductAdminResponseDto::new);
    }

    public ProductAdminResponseDto read(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return new ProductAdminResponseDto(product);
    }

    @Transactional
    public void delete(Long productId) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        
        for (ProductImage image : product.getImages()) {
            fileService.deleteFile(image.getStoredFilePath());
        }
        
        productRepository.delete(product);
    }
}
