package org.example.trusttrade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trusttrade.dto.SignUpRequest;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.login.dto.LogInRequest;
import org.example.trusttrade.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // 아이디 중복 체크
    public void verifyAccountDuplicate(String account) {

        log.debug("계정 중복 체크 요청 시작 account = {}", account);
        if (userRepository.findByUserAccount(account).isPresent()) {
            throw new DataIntegrityViolationException("이미 존재하는 계정입니다: " + account);
        }
        log.debug("계정 중복 체크 완료. account = {}", account);

    }


    // 회원가입
    public void signUp(SignUpRequest request) {

        try {
            // 비밀번호 암호화
            String encodedPw = passwordEncoder.encode(request.getPassword());
            // User 객체 생성
            User user = User.createUser(request, encodedPw);
            // 저장
            userRepository.save(user);
        } catch (Exception e){
            throw new IllegalStateException("회원가입 중 오류 발생");
        }

    }


    // 로그인
    public void userLogin(LogInRequest request) {
        // 아이디 존재 여부 확인
        User user = userRepository.findByUserAccount(request.getAccount())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getUserPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
