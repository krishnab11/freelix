package com.freelix.service;

import com.freelix.dto.RegisterDto;
import com.freelix.entity.User;
import com.freelix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public User register(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered: " + dto.getEmail());
        }
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        User saved = userRepository.save(user);
        emailService.sendWelcomeEmail(saved);
        return saved;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void updateProfile(User user, String name, String bio, String skills, String location, String phone) {
        user.setName(name);
        user.setBio(bio);
        user.setSkills(skills);
        user.setLocation(location);
        user.setPhone(phone);
        userRepository.save(user);
    }

    public void updateProfileImage(User user, String imageUrl) {
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }

    public void updateResume(User user, String resumeUrl) {
        user.setResumeUrl(resumeUrl);
        userRepository.save(user);
    }
}
