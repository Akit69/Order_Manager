package com.info.controller;

import com.info.entity.DTO.OrderCreateDTO;
import com.info.entity.PageResult;
import com.info.entity.Result;
import com.info.entity.VO.OrderVO;
import com.info.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderVO> create(@RequestBody OrderCreateDTO dto) {
        return Result.success(orderService.createOrder(dto));
    }

    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.getOrderPage(page, size, status));
    }

    @GetMapping("/detail/{orderId}")
    public Result<OrderVO> detail(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderDetail(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public Result<String> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return Result.success("已取消", null);
    }

    @PutMapping("/{orderId}/pay")
    public Result<String> pay(@PathVariable Long orderId) {
        orderService.payOrder(orderId);
        return Result.success("支付成功", null);
    }
}