package com.info.service.Impl;

import com.info.annotation.RateLimit;
import com.info.entity.*;
import com.info.entity.VO.SeckillResultVO;
import com.info.mapper.OrderItemMapper;
import com.info.mapper.OrderMapper;
import com.info.mapper.ProductMapper;
import com.info.mapper.SeckillActivityMapper;
import com.info.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final SeckillActivityMapper activityMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @RateLimit(permitsPerSecond = 500)
    public SeckillResultVO seckill(Long activityId) {
        Long userId = UserContext.getUserId().longValue();

        // 1. 校验活动
        SeckillActivity activity = activityMapper.selectById(activityId);
        if (activity == null || activity.getStatus() != 1) {
            throw new RuntimeException("活动不可用");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime()) || now.isAfter(activity.getEndTime())) {
            throw new RuntimeException("活动未在进行中");
        }

        // 2. 一人一单（Redis Set 去重）
        String userKey = "seckill:user:" + activityId;
        Boolean exists = redisTemplate.opsForSet().isMember(userKey, userId.toString());
        if (Boolean.TRUE.equals(exists)) {
            throw new RuntimeException("已参与过该活动");
        }

        // 3. Lua 脚本：检查库存 + 扣减 + 标记用户
        String lua = "local stock = redis.call('get', KEYS[1]) " +
                     "if not stock or tonumber(stock) <= 0 then return 0 end " +
                     "redis.call('decr', KEYS[1]) " +
                     "redis.call('sadd', KEYS[2], ARGV[1]) " +
                     "return 1";

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(lua, Long.class),
                Arrays.asList("seckill:stock:" + activityId, userKey),
                userId.toString());

        if (result == null || result == 0) {
            throw new RuntimeException("已售罄或已参与");
        }

        // 4. 生成订单（同步简化版）
        String orderNo = "SK" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                       + String.format("%04d", userId % 10000);

        Product product = productMapper.selectById(activity.getProductId());
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(activity.getSeckillPrice());
        order.setDiscountAmount(0L);
        order.setPayAmount(activity.getSeckillPrice());
        order.setStatus(0);
        orderMapper.insert(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setOrderNo(orderNo);
        item.setProductId(activity.getProductId());
        item.setProductName(product != null ? product.getName() : "秒杀商品");
        item.setPrice(activity.getSeckillPrice());
        item.setQuantity(1);
        item.setSubtotal(activity.getSeckillPrice());
        orderItemMapper.insert(item);

        // 5. 更新活动库存
        activity.setSeckillStock(activity.getSeckillStock() - 1);
        activityMapper.updateById(activity);

        return SeckillResultVO.builder()
                .orderNo(orderNo)
                .message("秒杀成功")
                .build();
    }
}
