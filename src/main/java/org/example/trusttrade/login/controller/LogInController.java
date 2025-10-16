package org.example.trusttrade.login.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.trusttrade.login.dto.LogInRequest;
import org.example.trusttrade.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LogInController {

    private final UserService userService;
    @PostMapping("")
    public ResponseEntity<String> userLogin(@RequestBody LogInRequest request) {
        userService.userLogin(request);
        return ResponseEntity.ok("로그인 성공");
    }

}
