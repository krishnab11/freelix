package com.freelix.config;

import com.freelix.entity.User;
import com.freelix.enums.Role;
import com.freelix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Seeds the database with a default admin account on first startup.
 * Admin credentials: admin@freelix.com / admin123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(DataInitializer.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@freelix.com")) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@freelix.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setBio("Freelix platform administrator");
            userRepository.save(admin);
            log.info("=== Default admin created: admin@freelix.com / admin123 ===");
        }

        // Optional: seed a sample client and freelancer for demo
        if (!userRepository.existsByEmail("client@demo.com")) {
            User client = new User();
            client.setName("Demo Client");
            client.setEmail("client@demo.com");
            client.setPassword(passwordEncoder.encode("demo123"));
            client.setRole(Role.CLIENT);
            client.setBio("Looking for talented freelancers");
            client.setLocation("New York, USA");
            userRepository.save(client);
            log.info("=== Demo client created: client@demo.com / demo123 ===");
        }

        if (!userRepository.existsByEmail("freelancer@demo.com")) {
            User freelancer = new User();
            freelancer.setName("Demo Freelancer");
            freelancer.setEmail("freelancer@demo.com");
            freelancer.setPassword(passwordEncoder.encode("demo123"));
            freelancer.setRole(Role.FREELANCER);
            freelancer.setBio("Full-stack developer with 5 years experience");
            freelancer.setSkills("Java, React, Spring Boot, PostgreSQL");
            freelancer.setLocation("London, UK");
            userRepository.save(freelancer);
            log.info("=== Demo freelancer created: freelancer@demo.com / demo123 ===");
        }
    }
}
