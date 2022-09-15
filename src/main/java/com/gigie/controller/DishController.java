package com.gigie.controller;

import com.alibaba.druid.support.spring.stat.SpringStatUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gigie.DTO.DishDto;
import com.gigie.domain.Category;
import com.gigie.domain.Dish;
import com.gigie.domain.DishFlavor;
import com.gigie.service.CategoryService;
import com.gigie.service.DishFlavorservice;
import com.gigie.service.DishService;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.management.LockInfo;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private  DishFlavorservice dishFlavorservice;
    @Autowired
    private CategoryService CategoryService;

    @PostMapping

    public R<Void> addnewdish(@RequestBody  DishDto dishDto)
    {
        log.info(dishDto.getImage());
        dishService.saveflavor(dishDto);
        return R.success(null);
    }

    @GetMapping("/page")
    public  R<Page<DishDto>> getPage(int page,int pageSize,String name)


    {
        Page<Dish> pageinfo=new Page<>(page,pageSize);
        Page<DishDto> dtoInfo=new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageinfo,queryWrapper);
        BeanUtils.copyProperties(pageinfo,dtoInfo,"records");
        List<Dish> records = pageinfo.getRecords();
        List<DishDto> list=new ArrayList<>();
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            Long categoryId = record.getCategoryId();
            Category byId = CategoryService.getById(categoryId);
            if (byId != null)
            {
                String name1 = byId.getName();
                dishDto.setCategoryName(name1);
            }

            list.add(dishDto);
        }



        dtoInfo.setRecords(list);

        return R.success(dtoInfo);

    }


    @DeleteMapping
    public R<Void> delete(@RequestParam List<Long> ids)
    {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish ::getId,ids);
        queryWrapper.eq(Dish ::getStatus,1);
        int count = dishService.count(queryWrapper);
        if (count > 0)
            return  R.error("菜品销售中,无法删除");

        dishService.removeByIds(ids);
        return  R.success(null);
    }


    @PostMapping("/status/{id}")
    public  R<Void> changestatus(@PathVariable int id,String ids)
    {
        log.info(id+"");
        log.info(ids);
        String[] split = ids.split(",");
        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(Dish ::getStatus,id);
        queryWrapper.in(Dish ::getId,split);
        dishService.update(queryWrapper);

        return R.success(null);

    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id)
    {
       return  R.success(dishService.getflavorbyid(id));

    }

    @PutMapping()

    public R<Void> updateDish(@RequestBody  DishDto dishDto)
    {
        log.info(dishDto.getImage());
        dishService.updatewithflavor(dishDto);
        return R.success(null);
    }

    @GetMapping("/list")
    public  R<List<Dish>> getList(Dish dish)
    {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish ::getCategoryId,dish.getCategoryId()).
                orderByDesc(Dish::getUpdateTime).eq(Dish::getStatus,1);//启售状态
        List<Dish> list = dishService.list(queryWrapper);


        return R.success(list);

    }
}
