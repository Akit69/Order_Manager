package com.info.service;

import com.info.entity.VO.CartVO;

public interface CartService {
    CartVO getCart();
    void addCart(Long productId, Integer quantity);
    void updateCart(Long productId, Integer quantity);
    void deleteCart(Long productId);
    void clearAll();
}
