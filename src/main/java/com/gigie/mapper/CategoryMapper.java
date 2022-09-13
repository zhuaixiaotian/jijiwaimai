package com.gigie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gigie.domain.Category;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
