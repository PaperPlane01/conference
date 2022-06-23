package org.paperplane.conference.controller;

import lombok.RequiredArgsConstructor;
import org.paperplane.conference.api.request.SignUpRequest;
import org.paperplane.conference.api.response.SignUpResponse;
import org.paperplane.conference.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/sign-up")
@RequiredArgsConstructor
public class SignUpController {
    private final UserService userService;

    @PostMapping
    public SignUpResponse signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return userService.signUp(signUpRequest);
    }
}
