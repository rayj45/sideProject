package jbch.org.sideproject.request.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductCreateRequestDto {
    private String name;
    private int price;
    private int stockQuantity;
    private String category;
    private String description;
    private List<MultipartFile> images;
}
