package com.info.service.Impl;

import com.info.entity.CouponTemplate;
import com.info.service.CouponCalculator;
import org.springframework.stereotype.Component;

@Component
public class DiscountCalculator implements CouponCalculator {
    @Override
    public long calculate(long totalAmount, CouponTemplate template) {
        return totalAmount * template.getDiscount() / 100;
    }
    @Override
    public Integer getType() { return 2; }
}