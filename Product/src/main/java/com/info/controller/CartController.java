package com.info.controller;

import com.info.entity.Result;
import com.info.entity.VO.CartVO;
import com.info.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<CartVO> getCart() {
        return Result.success(cartService.getCart());
    }

    @PostMapping
    public Result<String> add(@RequestBody Map<String, Object> body) {
        Long productId = Long.parseLong(body.get("productId").toString());
        Integer quantity = Integer.parseInt(body.get("quantity").toString());
        cartService.addCart(productId, quantity);
        return Result.success("添加成功", null);
    }

    @PutMapping
    public Result<String> update(@RequestBody Map<String, Object> body) {
        Long productId = Long.parseLong(body.get("productId").toString());
        Integer quantity = Integer.parseInt(body.get("quantity").toString());
        cartService.updateCart(productId, quantity);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{productId}")
    public Result<String> remove(@PathVariable Long productId) {
        cartService.deleteCart(productId);
        return Result.success("删除成功", null);
    }

    @DeleteMapping
    public Result<String> clear() {
        cartService.clearAll();
        return Result.success("已清空", null);
    }
}