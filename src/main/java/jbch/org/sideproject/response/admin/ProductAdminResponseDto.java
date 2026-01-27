package jbch.org.sideproject.response.admin;

import jbch.org.sideproject.domain.Product;
import jbch.org.sideproject.domain.ProductImage;
import jbch.org.sideproject.domain.ProductStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProductAdminResponseDto {
    private final Long id;
    private final String name;
    private final int price;
    private final int stockQuantity;
    private final String category;
    private final String description;
    private final int viewCount;
    private final ProductStatus status;
    private final String sellerName;
    private final LocalDateTime createdDate;
    private final String thumbnailPath;
    private final List<ImageInfo> images;

    @Getter
    public static class ImageInfo {
        private final Long id;
        private final String path;

        public ImageInfo(ProductImage image) {
            this.id = image.getId();
            this.path = image.getStoredFilePath();
        }
    }

    public ProductAdminResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.category = product.getCategory();
        this.description = product.getDescription();
        this.viewCount = product.getViewCount();
        this.status = product.getStatus();
        this.sellerName = product.getSeller().getNickName();
        this.createdDate = product.getCreatedDate();

        this.thumbnailPath = product.getImages().stream()
                .filter(ProductImage::isThumbnail)
                .findFirst()
                .map(ProductImage::getStoredFilePath)
                .orElse(null);

        this.images = product.getImages().stream()
                .map(ImageInfo::new)
                .collect(Collectors.toList());
    }
}
