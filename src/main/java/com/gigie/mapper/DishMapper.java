package com.gigie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.gigie.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
