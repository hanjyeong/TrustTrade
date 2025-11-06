package org.example.trusttrade.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.trusttrade.login.dto.LoginResponse;
import org.example.trusttrade.login.service.KakaoLoginService;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/kakao/login")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String code) throws JSONException {
        String redirectUri = "http://54.66.146.131:8080/auth/kakao/login";
        LoginResponse response = kakaoLoginService.kakaoLogin(code, redirectUri);
        return ResponseEntity.ok(response);
    }
}