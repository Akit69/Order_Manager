package com.info.controller;

import com.info.entity.DTO.CouponDTO;
import com.info.entity.PageResult;
import com.info.entity.Result;
import com.info.entity.VO.CouponVO;
import com.info.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/template")
    public Result<String> createTemplate(@Valid @RequestBody CouponDTO dto) {
        couponService.createTemplate(dto);
        return Result.success("Created", null);
    }

    @GetMapping("/template/list")
    public Result<PageResult<CouponVO>> templateList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(couponService.getTemplatePage(page, size));
    }

    @PostMapping("/receive/{templateId}")
    public Result<String> receive(@PathVariable Long templateId) {
        couponService.receive(templateId);
        return Result.success("Received", null);
    }

    @GetMapping("/my")
    public Result<List<CouponVO>> myCoupons() {
        return Result.success(couponService.getMyCoupons());
    }
}