package com.info.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.info.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}