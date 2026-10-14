package com.rssb.config;

import com.rssb.model.AppUser;
import com.rssb.repo.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedUsers(AppUserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByUsername("admin")) {
                users.save(new AppUser("admin", encoder.encode("Admin@123"), "ROLE_ADMIN"));
            }
            if (!users.existsByUsername("sewadar")) {
                users.save(new AppUser("sewadar", encoder.encode("Sewadar@123"), "ROLE_USER"));
            }
        };
    }
}
