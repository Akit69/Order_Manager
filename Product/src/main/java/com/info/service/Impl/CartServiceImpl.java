package com.info.service.Impl;

import com.info.entity.Product;
import com.info.entity.UserContext;
import com.info.entity.VO.CartItemVO;
import com.info.entity.VO.CartVO;
import com.info.mapper.ProductMapper;
import com.info.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductMapper productMapper;

    private String cartKey() {
        return "cart:" + UserContext.getUserId();
    }

    @Override
    public CartVO getCart() {
        String key = cartKey();
        Map<Object, Object> cartMap = redisTemplate.opsForHash().entries(key);
        if (cartMap.isEmpty()) {
            return CartVO.builder()
                    .items(Collections.emptyList())
                    .totalNum(0)
                    .totalPrice(0L)
                    .build();
        }

        List<Long> productIds = cartMap.keySet().stream()
                .map(k -> Long.parseLong(k.toString()))
                .collect(Collectors.toList());

        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        int totalNum = 0;
        long totalPrice = 0;
        List<CartItemVO> items = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : cartMap.entrySet()) {
            Long pid = Long.parseLong(entry.getKey().toString());
            Integer qty = Integer.parseInt(entry.getValue().toString());
            Product product = productMap.get(pid);

            if (product == null || product.getStatus() != 1) {
                continue;
            }

            long subtotal = product.getPrice() * qty;

            items.add(CartItemVO.builder()
                    .productId(pid)
                    .productName(product.getName())
                    .mainImage(product.getMainImage())
                    .price(product.getPrice())
                    .quantity(qty)
                    .subtotal(subtotal)
                    .build());

            totalNum += qty;
            totalPrice += subtotal;
        }

        return CartVO.builder()
                .items(items)
                .totalNum(totalNum)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void addCart(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new RuntimeException("商品不存在或已下架");
        }
        String key = cartKey();
        redisTemplate.opsForHash().increment(key, productId.toString(), quantity);
    }

    @Override
    public void updateCart(Long productId, Integer quantity) {
        String key = cartKey();
        redisTemplate.opsForHash().put(key, productId.toString(), quantity);
    }

    @Override
    public void deleteCart(Long productId) {
        String key = cartKey();
        redisTemplate.opsForHash().delete(key, productId.toString());
    }

    @Override
    public void clearAll() {
        String key = cartKey();
        redisTemplate.delete(key);
    }
}