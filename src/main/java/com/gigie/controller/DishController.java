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
import com.gigie.domain.SetmealDish;
import com.gigie.service.*;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private setmealdishservice setmealdishservice;
    @Autowired
    private SetmealService SetmealService;
    @PostMapping
    public R<String> addnewdish(@RequestBody  DishDto dishDto)
    {
        log.info(dishDto.getImage());
        dishService.saveflavor(dishDto);
        return R.success("添加菜品成功");
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

    @Transactional
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids)
    {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish ::getId,ids);
        queryWrapper.eq(Dish ::getStatus,1);
        int count = dishService.count(queryWrapper);
        if (count > 0)
            return  R.error("菜品销售中,无法删除");
        LambdaQueryWrapper<SetmealDish> queryWrap = new LambdaQueryWrapper<>();
        queryWrap.in(SetmealDish::getDishId, ids);
        List<SetmealDish> list = setmealdishservice.list(queryWrap);

        List<Long> setmeallist=null;
        if (list !=null && list.size() > 0)

        {
            Set<Long> set = new HashSet<>();
        //给套餐id去重
        for (SetmealDish setmealDish : list) {
            set.add(setmealDish.getSetmealId());
        }
        setmeallist = new ArrayList<>(set);

        }
        //删除菜品
        dishService.removeByIds(ids);
        LambdaUpdateWrapper<SetmealDish> q = new LambdaUpdateWrapper<>();
        q.in(SetmealDish ::getDishId,ids);
        setmealdishservice.remove(q);
        if (list==null || list.size() == 0)
            return R.success("删除成功");

        for (Long aLong : setmeallist) {
            LambdaQueryWrapper<SetmealDish> setmealDish=new LambdaQueryWrapper<>();
            setmealDish.eq(SetmealDish ::getSetmealId,aLong);
            List<SetmealDish> list1 = setmealdishservice.list(setmealDish);
            if (list1==null || list1.size() == 0)
            {
                SetmealService.removeById(aLong);
            }
        }


//        LambdaQueryWrapper<SetmealDish> queryWrap = new LambdaQueryWrapper<>();
//        queryWrap.in(SetmealDish::getDishId, ids);
//        List<SetmealDish> list = setmealdishservice.list(queryWrap);
//        if (list == null || list.size() == 0)
//            return R.success("修改状态成功");
//        Set<Long> set = new HashSet<>();
//        //给套餐id去重
//        for (SetmealDish setmealDish : list) {
//            set.add(setmealDish.getSetmealId());
//        }
//        List<Long> setmeallist = new ArrayList<>(set);
//        for (Long setmealid : setmeallist) {
//            LambdaQueryWrapper<SetmealDish> q=new LambdaQueryWrapper<>();
//            q.eq(SetmealDish ::getSetmealId,setmealid);
//            List<SetmealDish> list1 = setmealdishservice.list(q);
//            List<Long> dish=new ArrayList<>();
//            for (SetmealDish setmealDish : list1) {
//                dish.add(setmealDish.getDishId());
//            }
//            if (ids.containsAll(dish))
//            {
//                //删除菜品和 关联记录
//
//            }
//
//        }


        return  R.success("删除菜品成功");
    }


    @PostMapping("/status/{id}")
    @Transactional
    public  R<String> changestatus(@PathVariable int id,@RequestParam List<Long> ids)
    {


        // 给菜品停售
        dishService.changestatus(id,ids);
        if (id==0) {
            LambdaQueryWrapper<SetmealDish> queryWrap = new LambdaQueryWrapper<>();
            queryWrap.in(SetmealDish::getDishId, ids);
            List<SetmealDish> list = setmealdishservice.list(queryWrap);
            if (list == null || list.size() == 0) {
                return R.success("修改状态成功");
            }

            Set<Long> set = new HashSet<>();
            for (SetmealDish setmealDish : list) {
                set.add(setmealDish.getSetmealId());
            }
            List<Long> setmeallist = new ArrayList<>(set);
            SetmealService.changestatus(id, setmeallist);
        }

        return R.success("修改状态成功");

    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id)
    {
       return  R.success(dishService.getflavorbyid(id));

    }

    @PutMapping()

    public R<String> updateDish(@RequestBody  DishDto dishDto)
    {
        log.info(dishDto.getImage());
        dishService.updatewithflavor(dishDto);
        return R.success("更新菜品成功");
    }

//    @GetMapping("/list")
//    public  R<List<Dish>> getList(Dish dish)
//    {
//
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish ::getCategoryId,dish.getCategoryId()).
//                orderByDesc(Dish::getUpdateTime).eq(Dish::getStatus,1);//启售状态
//        List<Dish> list = dishService.list(queryWrapper);
//
//
//        return R.success(list);
//
//    }
//
        @GetMapping("/list")
        public  R<List<DishDto>> getList(Dish dish)
        {   LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish ::getCategoryId,dish.getCategoryId()).
             orderByDesc(Dish::getUpdateTime).eq(Dish::getStatus,1);//启售状态
            List<Dish> list = dishService.list(queryWrapper);
            List<DishDto> listDto = new ArrayList<>();
            BeanUtils.copyProperties(list,listDto);
            for (Dish dish1 : list) {
                LambdaQueryWrapper<DishFlavor> qo = new LambdaQueryWrapper<>();
                qo.eq(DishFlavor::getDishId,dish1.getId());
                List<DishFlavor> list1 = dishFlavorservice.list(qo);
                DishDto dishDto = new DishDto();
                BeanUtils.copyProperties(dish1,dishDto);
                dishDto.setFlavors(list1);
                listDto.add(dishDto);
            }




            return R.success(listDto);

        }
}
