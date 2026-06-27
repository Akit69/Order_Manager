package com.info.entity.DTO;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CouponDTO {
    @NotNull private String name;
    @NotNull private Integer type;
    @NotNull private Long condition;
    @NotNull private Long discount;
    @NotNull private Integer total;
    @NotNull private LocalDateTime startTime;
    @NotNull private LocalDateTime endTime;
}