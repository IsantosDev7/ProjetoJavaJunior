package com.example.portalaluno.shared;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.auth.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {
            boolean existeAdmin = userRepository.findByEmail(adminEmail).isPresent();

            if (!existeAdmin) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(UserRole.SUPER_ADMIN);

                userRepository.save(admin);
                System.out.println(">>> Super Admin criado com sucesso via Seed!");
            }
        };
    }
}