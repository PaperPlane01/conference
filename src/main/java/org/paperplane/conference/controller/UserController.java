package org.paperplane.conference.controller;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.response.UserResponse;
import org.paperplane.conference.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponse> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping(params = "displayedName")
    public List<UserResponse> findUsersByDisplayedName(@RequestParam String displayedName) {
        return userService.findUsersByDisplayedName(displayedName);
    }
}
