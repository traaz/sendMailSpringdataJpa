package com.project.emailValidation.controller;

import com.project.emailValidation.requests.RegisterRequest;
import com.project.emailValidation.requests.VerifyRequest;
import com.project.emailValidation.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    public String addUser(@RequestBody @Valid RegisterRequest registerRequest){
       return userService.addUser(registerRequest);
    }
    @PostMapping("/verify")
    public String verifyUser(@RequestBody VerifyRequest verifyRequest){
        return userService.verifyCode(verifyRequest);
    }
}
