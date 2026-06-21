package com.info.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Long price;
    private String description;
    private String mainImage;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private LocalDateTime createTime;
}
