package com.gigie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gigie.DTO.DishDto;
import com.gigie.domain.Dish;
import com.gigie.domain.DishFlavor;
import com.gigie.mapper.DishMapper;
import com.gigie.service.DishFlavorservice;
import com.gigie.service.DishService;
import com.gigie.utils.CustomException;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j

public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorservice dishFlavorservice;
    @Override
    @Transactional
    public void saveflavor(DishDto dishDto) {
        if (!this.save(dishDto))
        {
          throw new CustomException("添加菜品失败");
        }

        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id=dishDto.getId();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }

        if(!dishFlavorservice.saveBatch(flavors))
        {
            throw new CustomException("添加菜品口味失败");
        }


    }

    @Override
    public DishDto getflavorbyid(Long id) {
        Dish byId = this.getById(id);
        Long id1 = byId.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor ::getDishId,id1);
        List<DishFlavor> list = dishFlavorservice.list(queryWrapper);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(byId,dishDto);
        dishDto.setFlavors(list);
        return  dishDto;

    }

    @Override
    @Transactional
    public void updatewithflavor(DishDto dishDto) {
        if(!this.updateById(dishDto))
        {
            throw new CustomException("更新菜品失败");
        }
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorservice.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id=dishDto.getId();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }

        if(!dishFlavorservice.saveBatch(flavors))
        {
            throw new CustomException("更新菜品口味失败");
        }


    }

    @Override
    public void changestatus(int id,  List<Long> ids) {

        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(Dish ::getStatus,id);
        queryWrapper.in(Dish ::getId,ids);
        this.update(queryWrapper);

    }
}
