package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

@Service // marks this as a service holding business logic
public class ProfileService
{
    private final ProfileRepository profileRepository; // talks to the profiles table via JPA

    // Constructor
    public ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository; // injects the profile repository
    }

    // Create profile
    public Profile create(Profile profile)
    {
        return profileRepository.save(profile); // inserts the new profile when a user registers
    }

    // Get profile by user id
    public Profile getByUserId(int userId)
    {
        return profileRepository.findById(userId).orElse(null); // returns the profile or null if not found
    }

    // Update profile
    public Profile update(int userId, Profile profile)
    {
        profile.setUserId(userId); // makes sure the id is set so it updates instead of inserts
        return profileRepository.save(profile); // saves the changes and returns the updated profile
    }
}