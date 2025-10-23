package org.example.trusttrade.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NewNotiResDto {

    private Long notiId;
    private UUID userId;
    private String content;
    private LocalDateTime date;
    private Boolean isRead;

}
