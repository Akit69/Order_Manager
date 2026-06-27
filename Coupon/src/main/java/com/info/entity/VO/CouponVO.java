package com.info.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CouponVO {
    private Long couponId;
    private Long templateId;
    private String name;
    private Integer type;
    private String typeDesc;
    private Long condition;
    private Long discount;
    private Integer status;
    private String statusDesc;
    private LocalDateTime endTime;
}