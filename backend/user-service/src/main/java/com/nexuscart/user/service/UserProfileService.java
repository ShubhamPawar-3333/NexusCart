package com.nexuscart.user.service;

import com.nexuscart.user.entity.UserProfile;
import com.nexuscart.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfile getProfile(UUID userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    public UserProfile getProfileByEmail(String email) {
        return userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    @Transactional
    public UserProfile createProfile(UserProfile profile) {
        if (userProfileRepository.existsByEmail(profile.getEmail())) {
            throw new RuntimeException("Profile already exists for this email");
        }
        log.info("Creating profile for user: {}", profile.getEmail());
        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile updateProfile(UUID userId, UserProfile updates) {
        UserProfile existing = getProfile(userId);

        if (updates.getFirstName() != null)
            existing.setFirstName(updates.getFirstName());
        if (updates.getLastName() != null)
            existing.setLastName(updates.getLastName());
        if (updates.getPhoneNumber() != null)
            existing.setPhoneNumber(updates.getPhoneNumber());
        if (updates.getAvatarUrl() != null)
            existing.setAvatarUrl(updates.getAvatarUrl());
        if (updates.getDateOfBirth() != null)
            existing.setDateOfBirth(updates.getDateOfBirth());
        if (updates.getGender() != null)
            existing.setGender(updates.getGender());

        log.info("Updated profile for user: {}", userId);
        return userProfileRepository.save(existing);
    }
}
