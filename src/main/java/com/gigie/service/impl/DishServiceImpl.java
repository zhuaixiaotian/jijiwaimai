package com.gigie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gigie.DTO.DishDto;
import com.gigie.domain.Dish;
import com.gigie.domain.DishFlavor;
import com.gigie.mapper.DishMapper;
import com.gigie.service.DishFlavorservice;
import com.gigie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j

public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorservice dishFlavorservice;
    @Override
    @Transactional
    public void saveflavor(DishDto dishDto) {
        this.save(dishDto);

        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id=dishDto.getId();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }

        dishFlavorservice.saveBatch(flavors);


    }
}
