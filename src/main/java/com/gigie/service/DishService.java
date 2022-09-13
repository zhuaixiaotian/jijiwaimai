package com.gigie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gigie.DTO.DishDto;
import com.gigie.domain.Dish;

public interface DishService extends IService<Dish> {
    void saveflavor(DishDto dishDto);
}
