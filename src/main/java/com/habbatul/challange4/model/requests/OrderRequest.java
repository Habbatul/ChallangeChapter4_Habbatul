package com.habbatul.challange4.model.requests;

import com.habbatul.challange4.enums.OrderStatus;
import com.habbatul.challange4.model.responses.OrderDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private String destinationAddress;
    private OrderStatus completed;
    private List<OrderDetailRequest> detailOrder;
}
