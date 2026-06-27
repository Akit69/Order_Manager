package com.info.entity.DTO;

import lombok.Data;

@Data
public class OrderCreateDTO {
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
}