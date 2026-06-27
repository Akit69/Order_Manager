package com.info.service.Impl;

import com.info.service.CouponCalculator;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CouponCalculatorFactory {
    private final Map<Integer, CouponCalculator> calculatorMap;

    public CouponCalculatorFactory(List<CouponCalculator> calculators) {
        calculatorMap = calculators.stream()
                .collect(Collectors.toMap(CouponCalculator::getType, c -> c));
    }

    public CouponCalculator getCalculator(Integer type) {
        CouponCalculator c = calculatorMap.get(type);
        if (c == null) throw new RuntimeException("未知优惠券类型: " + type);
        return c;
    }
}
