package com.info.service.Impl;

import com.info.entity.CouponTemplate;
import com.info.service.CouponCalculator;
import org.springframework.stereotype.Component;

@Component
public class FullReductionCalculator implements CouponCalculator {
    @Override
    public long calculate(long totalAmount, CouponTemplate template) {
        if (totalAmount < template.getCondition()) {
            throw new RuntimeException("Order amount does not meet coupon minimum");
        }
        return template.getDiscount();
    }
    @Override
    public Integer getType() { return 1; }
}