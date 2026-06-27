package com.info.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.info.entity.*;
import com.info.entity.DTO.CouponDTO;
import com.info.entity.VO.CouponVO;
import com.info.mapper.CouponTemplateMapper;
import com.info.mapper.UserCouponMapper;
import com.info.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponTemplateMapper templateMapper;
    private final UserCouponMapper userCouponMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    // ===== create template (ADMIN) =====
    @Override
    @Transactional
    public void createTemplate(CouponDTO dto) {
        CouponTemplate t = new CouponTemplate();
        t.setName(dto.getName());
        t.setType(dto.getType());
        t.setCondition(dto.getCondition());
        t.setDiscount(dto.getDiscount());
        t.setTotal(dto.getTotal());
        t.setReceived(0);
        t.setStartTime(dto.getStartTime());
        t.setEndTime(dto.getEndTime());
        t.setStatus(1);
        templateMapper.insert(t);
        // init Redis stock
        redisTemplate.opsForValue().set("coupon:stock:" + t.getId(), dto.getTotal());
    }

    // ===== template list =====
    @Override
    public PageResult<CouponVO> getTemplatePage(Integer page, Integer size) {
        LambdaQueryWrapper<CouponTemplate> w = new LambdaQueryWrapper<>();
        w.eq(CouponTemplate::getStatus, 1);
        w.orderByDesc(CouponTemplate::getId);
        IPage<CouponTemplate> p = templateMapper.selectPage(new Page<>(page, size), w);

        List<CouponVO> list = p.getRecords().stream()
                .map(t -> CouponVO.builder()
                        .templateId(t.getId())
                        .name(t.getName())
                        .type(t.getType())
                        .typeDesc(t.getType() == 1 ? "Full Reduction" : "Discount")
                        .condition(t.getCondition())
                        .discount(t.getDiscount())
                        .endTime(t.getEndTime())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(list, p.getTotal(), (int) p.getCurrent(), (int) p.getSize());
    }

    // ===== receive coupon =====
    @Override
    public void receive(Long templateId) {
        Long userId = UserContext.getUserId().longValue();

        // check valid
        CouponTemplate t = templateMapper.selectById(templateId);
        if (t == null || t.getStatus() != 1) throw new RuntimeException("Coupon not available");
        if (LocalDateTime.now().isAfter(t.getEndTime())) throw new RuntimeException("Coupon expired");

        // one per user (Redis Set)
        String userKey = "coupon:user:" + userId;
        Boolean exists = redisTemplate.opsForSet().isMember(userKey, templateId.toString());
        if (Boolean.TRUE.equals(exists)) throw new RuntimeException("Already received this coupon");

        // stock check
        Long remain = redisTemplate.opsForValue().decrement("coupon:stock:" + templateId);
        if (remain < 0) {
            redisTemplate.opsForValue().increment("coupon:stock:" + templateId);
            throw new RuntimeException("Coupon sold out");
        }

        // save
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setTemplateId(templateId);
        uc.setStatus(0);
        userCouponMapper.insert(uc);

        redisTemplate.opsForSet().add(userKey, templateId.toString());
    }

    // ===== my coupons =====
    @Override
    public List<CouponVO> getMyCoupons() {
        Long userId = UserContext.getUserId().longValue();

        LambdaQueryWrapper<UserCoupon> w = new LambdaQueryWrapper<>();
        w.eq(UserCoupon::getUserId, userId);
        w.eq(UserCoupon::getStatus, 0);
        List<UserCoupon> list = userCouponMapper.selectList(w);

        return list.stream().map(uc -> {
            CouponTemplate t = templateMapper.selectById(uc.getTemplateId());
            return CouponVO.builder()
                    .couponId(uc.getId())
                    .templateId(uc.getTemplateId())
                    .name(t != null ? t.getName() : "")
                    .type(t != null ? t.getType() : 0)
                    .typeDesc(t != null && t.getType() == 1 ? "Full Reduction" : "Discount")
                    .condition(t != null ? t.getCondition() : 0L)
                    .discount(t != null ? t.getDiscount() : 0L)
                    .status(uc.getStatus())
                    .statusDesc("Available")
                    .endTime(t != null ? t.getEndTime() : null)
                    .build();
        }).collect(Collectors.toList());
    }
}