package com.gigie.controller;

import com.alibaba.druid.support.spring.stat.SpringStatUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gigie.DTO.SetmealDto;
import com.gigie.domain.Category;
import com.gigie.domain.Dish;
import com.gigie.domain.Setmeal;
import com.gigie.domain.SetmealDish;
import com.gigie.service.CategoryService;
import com.gigie.service.SetmealService;
import com.gigie.service.setmealdishservice;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService SetmealService;
    @Autowired
    private setmealdishservice setmealdishservice;
    @Autowired
    private CategoryService CategoryService;
    @PostMapping
    public R<Void> addsetmeal(@RequestBody SetmealDto setmealDto)
    {
        SetmealService.savemeal_dish(setmealDto);
        return R.success(null);

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
    public  R<Void> changestatus(@PathVariable int id,String ids)
    {
        log.info(id+"");
        log.info(ids);
        String[] split = ids.split(",");
        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(Setmeal ::getStatus,id);
        queryWrapper.in(Setmeal ::getId,split);
        SetmealService.update(queryWrapper);

        return R.success(null);

    }
    @DeleteMapping
    @Transactional
    public R<Void> delete(@RequestParam List<Long> ids)
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

        return  R.success(null);
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
    public  R<Void> update(@RequestBody SetmealDto setmealDto) {
        SetmealService.updatedish(setmealDto);
        return R.success(null);
    }


}
