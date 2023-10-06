package com.habbatul.challange4.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class OrderResponse {
    private LocalDateTime orderTime;
    private String destinationAddress;
    private String completed;
    private List<OrderDetailResponse> detailOrder;
    private String pembeliName;
}
