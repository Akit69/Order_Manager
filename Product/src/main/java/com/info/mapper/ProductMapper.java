package com.info.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.info.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}