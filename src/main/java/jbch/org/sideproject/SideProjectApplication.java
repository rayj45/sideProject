package jbch.org.sideproject;

import jbch.org.sideproject.domain.Faq;
import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.domain.UserRole;
import jbch.org.sideproject.domain.UserStatus;
import jbch.org.sideproject.repository.FaqRepository;
import jbch.org.sideproject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling // 스케줄링 활성화
@EnableJpaAuditing
@SpringBootApplication
public class SideProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SideProjectApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, FaqRepository faqRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Add admin user if not exists
            if (userRepository.findByEmail("rayj45@naver.com").isEmpty()) {
                User adminUser = User.builder()
                        .email("rayj45@naver.com")
                        .nickName("테스트관리자")
                        .password(passwordEncoder.encode("admin"))
                        .role(UserRole.ROLE_ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(adminUser);
            }

            // Add sample FAQs if table is empty
            if (faqRepository.count() == 0) {
                Faq faq1 = Faq.builder()
                        .question("이 사이트는 어떤 목적으로 만들어졌나요?")
                        .answer("이 사이트는 Spring Boot와 JPA를 사용하여 웹 애플리케이션을 개발하는 과정을 학습하고, 그 결과물을 기록하기 위한 개인 프로젝트입니다.")
                        .build();
                faqRepository.save(faq1);

                Faq faq2 = Faq.builder()
                        .question("공지사항과 자료실은 누구나 작성할 수 있나요?")
                        .answer("아니요, 현재는 관리자 권한을 가진 사용자만 글을 작성하거나 자료를 업로드할 수 있습니다. 일반 사용자는 열람 및 다운로드만 가능합니다.")
                        .build();
                faqRepository.save(faq2);
            }
        };
    }
}
