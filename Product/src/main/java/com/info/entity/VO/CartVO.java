package com.info.entity.VO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartVO {
    private List<CartItemVO> items;
    private Integer totalNum;
    private Long totalPrice;
}
