package com.habbatul.challange4.model.responses;

import com.habbatul.challange4.enums.OrderStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class OrderResponse {
    private LocalDateTime orderTime;
    private String destinationAddress;
    private OrderStatus completed;
    private List<OrderDetailResponse> detailOrder;
    private String pembeliName;
}
