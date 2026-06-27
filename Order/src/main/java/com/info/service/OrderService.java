package com.info.service;

import com.info.entity.DTO.OrderCreateDTO;
import com.info.entity.PageResult;
import com.info.entity.VO.OrderVO;

public interface OrderService {
    OrderVO createOrder(OrderCreateDTO dto);
    PageResult<OrderVO> getOrderPage(Integer page, Integer size, Integer status);
    OrderVO getOrderDetail(Long orderId);
    void cancelOrder(Long orderId);
    void payOrder(Long orderId);
}