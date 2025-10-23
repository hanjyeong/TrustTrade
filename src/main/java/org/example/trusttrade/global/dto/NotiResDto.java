package org.example.trusttrade.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.trusttrade.global.domain.order.Notification;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotiResDto {
    private String content;
    private UUID userId;

    public NotiResDto(Notification noti) {
        this.content = noti.getContent();
        this.userId = noti.getUser().getId();
    }
}
