package com.info.service;

import com.info.entity.CouponTemplate;

public interface CouponCalculator {
    long calculate(long totalAmount, CouponTemplate template);
    Integer getType();
}