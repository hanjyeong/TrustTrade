package org.example.trusttrade.service;


import org.example.trusttrade.global.service.NotificationService;
import org.example.trusttrade.login.domain.User;
import org.example.trusttrade.global.domain.order.Notification;
import org.example.trusttrade.global.dto.NotificationForm;
import org.example.trusttrade.global.repository.NotificationRepository;
import org.example.trusttrade.login.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class NotificationTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    //알림 생성 및 조회
    @Test
    public void createNoti(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("hyunjiin@google.com")
                .profileImage("img")
                .role(User.Role.USER)
                .memberType(User.MemberType.BUSINESS)
                .businessNumber("02-123-1234")
                .createdAt(LocalDateTime.now())
                .roughAddress("suwonsi")
                .build();

        userRepository.save(user);

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email("hyunjin2@google.com")
                .profileImage("img2")
                .role(User.Role.USER)
                .memberType(User.MemberType.BUSINESS)
                .businessNumber("02-123-12345")
                .createdAt(LocalDateTime.now())
                .roughAddress("suwonsi")
                .build();
        userRepository.save(user2);


        NotificationForm request = new NotificationForm();
        request.setContent("원목의자 상품 입금이 완료되었습니다. 지금 바로 상품을 발송해주세요.");
        request.setUserId(user.getId());

        Notification noti =  notificationService.createNotification(request.getContent(), request.getUserId());

        Notification find = notificationRepository.findById(noti.getId()).orElse(null);
        assertEquals(noti.getId(), find.getId());

        System.out.println("noti 생성 성공");

        List<Notification> noties = notificationService.getnotiesByUserId(find.getUser().getId());

        for(Notification noti1 : noties) {
            System.out.println(noti1.getContent());
        }
        System.out.println("noties 조회 성공");



    }
}
