package com.gigie.controller;

import com.alibaba.druid.support.spring.stat.SpringStatUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gigie.DTO.DishDto;
import com.gigie.DTO.SetmealDto;
import com.gigie.domain.*;
import com.gigie.service.*;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.PortableServer.LifespanPolicyOperations;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService SetmealService;
    @Autowired
    private setmealdishservice setmealdishservice;
    @Autowired
    private CategoryService CategoryService;
    @Autowired
    private DishFlavorservice DishFlavorservice;

    @PostMapping
    public R<String> addsetmeal(@RequestBody SetmealDto setmealDto)
    {
        SetmealService.savemeal_dish(setmealDto);

        return R.success("添加套餐成功");

    }

    @GetMapping("/page")

    public  R<Page<SetmealDto>> getPage(int page, int pageSize, String name)
    {

        log.info("select  page");
        Page<Setmeal> pageInfo =new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name)
                .orderByDesc(Setmeal ::getUpdateTime);
        SetmealService.page(pageInfo,queryWrapper);
        Page<SetmealDto> pageDto=new Page<>();
        BeanUtils.copyProperties(pageInfo, pageDto,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = new ArrayList<>();
        for (Setmeal record : records) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);
            Long categoryId = record.getCategoryId();
            Category byId = CategoryService.getById(categoryId);
            if (byId != null)
            {
                setmealDto.setCategoryName(byId.getName());

            }
            list.add(setmealDto);

        }
            pageDto.setRecords(list);
        return  R.success(pageDto);


    }


    @PostMapping("/status/{id}")
    @Transactional
    public  R<String> changestatus(@PathVariable int id,@RequestParam List<Long> ids)
    {
        // 给套餐停售卖
        SetmealService.changestatus(id,ids);
        if (id==1) {

            //给菜品停售
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
            List<SetmealDish> list = setmealdishservice.list(setmealDishLambdaQueryWrapper);
            List<Long> dishid = new ArrayList<>();
            log.info("dishid{}", dishid);
            for (SetmealDish setmealDish : list) {
                dishid.add(setmealDish.getDishId());
            }
            //给菜品启售
            dishService.changestatus(id,dishid);
        }




        return R.success("修改套餐状态成功");

    }
    @DeleteMapping
    @Transactional
    public R<String> delete(@RequestParam List<Long> ids)
    {
        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.in(Setmeal ::getId,ids);
        queryWrapper.eq(Setmeal ::getStatus,1);
        int count = SetmealService.count(queryWrapper);
        if (count > 0)
            return  R.error("套餐销售中,无法删除");


        SetmealService.removeByIds(ids);
        LambdaUpdateWrapper<SetmealDish> queryWrap = new LambdaUpdateWrapper<>();
        queryWrap.in(SetmealDish ::getSetmealId,ids);
        setmealdishservice.remove(queryWrap);

        return  R.success("删除套餐成功");
    }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getData(@PathVariable Long id){
        SetmealDto setmealDto = SetmealService.getDate(id);

        return R.success(setmealDto);
    }
    @PutMapping
    public  R<String> update(@RequestBody SetmealDto setmealDto) {
        SetmealService.updatedish(setmealDto);
        return R.success("更新套餐成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> getList(Long categoryId,int status)
    {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal ::getCategoryId,categoryId);
        queryWrapper.eq(Setmeal ::getStatus,status);
        queryWrapper.orderByDesc(Setmeal ::getUpdateTime);
        List<Setmeal> list = SetmealService.list(queryWrapper);

        return R.success(list);
    }

    @GetMapping("/dish/{id}")
    public  R<List<DishDto>> getDish(@PathVariable Long id)
    {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish ::getSetmealId,id);
        List<SetmealDish> list = setmealdishservice.list(queryWrapper);
        List<DishDto> listDTO=new ArrayList<>();
        for (SetmealDish setmealDish : list) {
            Dish dish = dishService.getById(setmealDish.getDishId());
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            dishDto.setCopies(setmealDish.getCopies());
            // 不返回口味
//            LambdaQueryWrapper<DishFlavor> queryWrap=new LambdaQueryWrapper<>();
//            queryWrap.eq(DishFlavor::getDishId,dish.getId());
//            List<DishFlavor> list1 = DishFlavorservice.list(queryWrap);
//            dishDto.setFlavors(list1);
            listDTO.add(dishDto);
        }
            return  R.success(listDTO);

    }



}
