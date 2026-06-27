package com.info.controller;

import com.info.entity.Result;
import com.info.entity.VO.SeckillResultVO;
import com.info.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {
    private final SeckillService seckillService;

    @PostMapping("/{activityId}")
    public Result<SeckillResultVO> seckill(@PathVariable Long activityId) {
        return Result.success(seckillService.seckill(activityId));
    }
}