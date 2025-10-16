package org.example.trusttrade.controller;

import lombok.RequiredArgsConstructor;
import org.example.trusttrade.dto.AccountDto;
import org.example.trusttrade.dto.SignUpRequest;
import org.example.trusttrade.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 아이디 중복 체크
    @PostMapping("/account/verify-duplicate")
    public ResponseEntity<String> verifyAccountDuplicate(@RequestBody AccountDto request) {
        userService.verifyAccountDuplicate(request.getAccount());
        return new ResponseEntity<>("사용 가능한 아이디 입니다", HttpStatus.OK);
    }

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<String> SignUp (@RequestBody SignUpRequest request)  {
        userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }






}
