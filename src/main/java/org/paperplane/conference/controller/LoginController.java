package org.paperplane.conference.controller;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.request.LoginRequest;
import org.paperplane.conference.api.response.LoginResponse;
import org.paperplane.conference.service.AuthorizationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {
    private final AuthorizationService authorizationService;

    @PostMapping
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return authorizationService.login(loginRequest);
    }
}
