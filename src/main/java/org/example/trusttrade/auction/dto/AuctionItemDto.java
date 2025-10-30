
package org.example.trusttrade.auction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.trusttrade.item.dto.request.AbstractItemImageDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionItemDto extends AbstractItemImageDto {
    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private UUID sellerId;

    @Size(max = 3, message = "카테고리는 최대 3개까지 선택할 수 있습니다.")
    private List<Integer> categoryIds;

    @NotNull
    private Integer startPrice;

    @NotNull
    private Integer bidUnit;

    @NotNull
    private String address;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Future(message = "종료 시간은 현재 이후여야 합니다.")
    private LocalDateTime endTime;
}
