package org.example.trusttrade.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.dto.AccountDto;
import org.example.trusttrade.dto.SignUpRequest;
import org.example.trusttrade.login.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // 아이디 중복 체크
    @PostMapping("/account/verify-duplicate")
    public ResponseEntity<String> verifyAccountDuplicate(@RequestBody AccountDto request) {
        try {
            userService.verifyAccountDuplicate(request.getAccount());
            return ResponseEntity.ok("사용 가능한 아이디 입니다");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 존재하는 아이디입니다");
        }
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
        try{
            userService.signUp(request);
        } catch (DataIntegrityViolationException e) {
            log.warn("회원가입 실패 - 중복 계정: {}", request.getAccount(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 회원입니다.");
        } catch (Exception e) {
            log.error("회원가입 실패 - 알 수 없는 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

}
