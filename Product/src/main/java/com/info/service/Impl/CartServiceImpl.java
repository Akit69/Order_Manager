package com.info.service.Impl;

import com.info.entity.VO.CartVO;
import com.info.service.CartService;
import lombok.RequiredArgsConstructor;
import com.info.mapper.ProductMapper;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductMapper productMapper;

    @Override
    public CartVO getCart(Long userId) {
        return null;
    }

    @Override
    public void addCart(Long productId, Integer quantity) {

    }

    @Override
    public void updateCart(Long productId, Integer quantity) {

    }

    @Override
    public void deleteCart(Long productId) {

    }

    @Override
    public void clearAll(Long userId) {

    }
}
