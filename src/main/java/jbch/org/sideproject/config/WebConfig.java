package jbch.org.sideproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** URL이 요청되면, file:///C:/workspace/sideProject/uploads/ 경로에서 파일을 찾도록 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + uploadDir + "/");
    }
}
