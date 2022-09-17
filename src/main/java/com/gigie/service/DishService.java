package com.gigie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gigie.DTO.DishDto;
import com.gigie.domain.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    void saveflavor(DishDto dishDto);

    DishDto getflavorbyid(Long id);

    void updatewithflavor(DishDto dishDto);

    void changestatus(int id, List<Long> ids);
}
