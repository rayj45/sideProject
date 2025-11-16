package jbch.org.sideproject;

import jbch.org.sideproject.domain.User;
import jbch.org.sideproject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaAuditing
@SpringBootApplication
public class SideProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SideProjectApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .build();
                userRepository.save(adminUser);
            }
        };
    }
}
