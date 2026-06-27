package com.info.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_coupon_template")
public class CouponTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer type;        // 1=满减 2=折扣
    private Long condition;      // 门槛（分）
    private Long discount;       // 满减=减多少分  折扣=百分比 80=8折
    private Integer total;
    private Integer received;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
}
