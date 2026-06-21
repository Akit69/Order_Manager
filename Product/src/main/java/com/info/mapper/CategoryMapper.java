package com.info.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.info.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}