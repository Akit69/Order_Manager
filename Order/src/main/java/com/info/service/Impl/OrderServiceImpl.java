package com.info.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.info.entity.*;
import com.info.entity.DTO.OrderCreateDTO;
import com.info.entity.VO.OrderItemVO;
import com.info.entity.VO.OrderVO;
import com.info.mapper.OrderItemMapper;
import com.info.mapper.OrderMapper;
import com.info.mapper.ProductMapper;
import com.info.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private String cartKey() {
        return "cart:" + UserContext.getUserId().longValue();
    }

    // ==================== create order ====================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO dto) {
        Long userId = UserContext.getUserId().longValue();

        // 1. read cart from Redis
        Map<Object, Object> cartMap = redisTemplate.opsForHash().entries(cartKey());
        if (cartMap.isEmpty()) {
            throw new RuntimeException("Shopping cart is empty");
        }

        // 2. collect productIds, batch query products
        List<Long> productIds = cartMap.keySet().stream()
                .map(k -> Long.parseLong(k.toString()))
                .collect(Collectors.toList());

        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 3. validate stock + Redis pre-deduct
        for (Map.Entry<Object, Object> entry : cartMap.entrySet()) {
            Long pid = Long.parseLong(entry.getKey().toString());
            Integer qty = Integer.parseInt(entry.getValue().toString());
            Product product = productMap.get(pid);

            if (product == null || product.getStatus() != 1) {
                throw new RuntimeException("Product [" + pid + "] is offline or not found");
            }

            Long remain = redisTemplate.opsForValue()
                    .decrement("product:stock:" + pid, qty);
            if (remain < 0) {
                rollbackStock(cartMap);
                throw new RuntimeException("Product [" + product.getName() + "] out of stock");
            }
        }

        // 4. calculate amounts
        long totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : cartMap.entrySet()) {
            Long pid = Long.parseLong(entry.getKey().toString());
            Integer qty = Integer.parseInt(entry.getValue().toString());
            Product product = productMap.get(pid);

            long subtotal = product.getPrice() * qty;
            totalAmount += subtotal;

            OrderItem item = new OrderItem();
            item.setProductId(pid);
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(qty);
            item.setSubtotal(subtotal);
            orderItems.add(item);
        }

        long discountAmount = 0;
        long payAmount = totalAmount - discountAmount;

        // 5. generate order number
        String orderNo = generateOrderNo();

        // 6. insert order
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setStatus(0);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        // 7. batch insert items
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            item.setOrderNo(orderNo);
            orderItemMapper.insert(item);
        }

        // 8. clear cart
        redisTemplate.delete(cartKey());

        // 9. build response
        List<OrderItemVO> itemVOs = orderItems.stream()
                .map(item -> OrderItemVO.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderVO.builder()
                .orderId(order.getId())
                .orderNo(orderNo)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .payAmount(payAmount)
                .status(0)
                .statusDesc("Unpaid")
                .receiverName(dto.getReceiverName())
                .receiverPhone(dto.getReceiverPhone())
                .receiverAddress(dto.getReceiverAddress())
                .remark(dto.getRemark())
                .items(itemVOs)
                .createdAt(order.getCreatedAt())
                .build();
    }

    private void rollbackStock(Map<Object, Object> cartMap) {
        cartMap.forEach((k, v) -> {
            Long pid = Long.parseLong(k.toString());
            Integer qty = Integer.parseInt(v.toString());
            redisTemplate.opsForValue().increment("product:stock:" + pid, qty);
        });
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uid = String.format("%04d", UserContext.getUserId().longValue() % 10000);
        String rand = String.format("%03d", new Random().nextInt(1000));
        return date + uid + rand;
    }

    // ==================== order list ====================
    @Override
    public PageResult<OrderVO> getOrderPage(Integer page, Integer size, Integer status) {
        Long userId = UserContext.getUserId().longValue();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        IPage<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(order -> OrderVO.builder()
                        .orderId(order.getId())
                        .orderNo(order.getOrderNo())
                        .totalAmount(order.getTotalAmount())
                        .discountAmount(order.getDiscountAmount())
                        .payAmount(order.getPayAmount())
                        .status(order.getStatus())
                        .statusDesc(getStatusDesc(order.getStatus()))
                        .createdAt(order.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(voList, orderPage.getTotal(),
                (int) orderPage.getCurrent(), (int) orderPage.getSize());
    }

    // ==================== order detail ====================
    @Override
    public OrderVO getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);

        List<OrderItemVO> itemVOs = items.stream()
                .map(item -> OrderItemVO.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderVO.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .statusDesc(getStatusDesc(order.getStatus()))
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .remark(order.getRemark())
                .items(itemVOs)
                .createdAt(order.getCreatedAt())
                .build();
    }

    // ==================== cancel order ====================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("Only unpaid orders can be cancelled");
        }

        order.setStatus(5);
        orderMapper.updateById(order);

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);

        for (OrderItem item : items) {
            redisTemplate.opsForValue()
                    .increment("product:stock:" + item.getProductId(), item.getQuantity());
        }
    }

    // ==================== pay order ====================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("Only unpaid orders can be paid");
        }

        order.setStatus(1);
        orderMapper.updateById(order);

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);

        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() - item.getQuantity());
                product.setSales(product.getSales() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
    }

    private String getStatusDesc(Integer status) {
        switch (status) {
            case 0: return "Unpaid";
            case 1: return "Paid";
            case 2: return "Shipped";
            case 3: return "Completed";
            case 5: return "Cancelled";
            default: return "Unknown";
        }
    }
}