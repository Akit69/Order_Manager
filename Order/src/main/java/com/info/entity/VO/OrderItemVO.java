package com.info.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {
    private Long productId;
    private String productName;
    private String productImage;
    private Long price;
    private Integer quantity;
    private Long subtotal;
}