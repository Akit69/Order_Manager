package com.info.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemVO {

    private Long productId;
    private String productName;
    private String mainImage;
    private Long price;         // 分
    private Integer quantity;
    private Long subtotal;

}
