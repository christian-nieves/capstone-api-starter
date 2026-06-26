package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.service.ProfileService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController // makes this class a rest controller
@RequestMapping("profile") // base url is profile
@CrossOrigin // allows the frontend website to call these endpoints
@PreAuthorize("isAuthenticated()") // every endpoint here requires the user to be logged in
public class ProfileController
{
    private ProfileService profileService; // handles profile business logic
    private UserService userService; // used to look up the logged in user

    // Constructor
    @Autowired
    public ProfileController(ProfileService profileService, UserService userService)
    {
        this.profileService = profileService; // injects the profile service
        this.userService = userService; // injects the user service
    }

    // Get profile
    @GetMapping // get request to profile
    public Profile getProfile(Principal principal)
    {
        String userName = principal.getName(); // gets the logged in user's username
        User user = userService.getByUserName(userName); // looks up the full user record
        int userId = user.getId(); // pulls their user id

        return profileService.getByUserId(userId); // returns that user's profile
    }

    // Update profile
    @PutMapping // put request to profile
    public Profile updateProfile(@RequestBody Profile profile, Principal principal)
    {
        String userName = principal.getName(); // gets the logged in user's username
        User user = userService.getByUserName(userName); // looks up the full user record
        int userId = user.getId(); // pulls their user id

        return profileService.update(userId, profile); // updates and returns the profile
    }
}