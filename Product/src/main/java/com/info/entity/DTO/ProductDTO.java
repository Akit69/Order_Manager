package com.info.entity.DTO;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ProductDTO {
    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "商品分类不能为空")
    private Long categoryId;

    @NotNull(message = "商品价格不能为空")
    @Min(value = 0, message = "商品价格不能小于0")
    private Long price;

    private String description;
    private String mainImage;

    @NotBlank(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负")
    private Integer stock;
}
