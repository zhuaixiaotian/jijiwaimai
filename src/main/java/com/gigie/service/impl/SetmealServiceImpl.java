package com.gigie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gigie.DTO.SetmealDto;
import com.gigie.domain.Setmeal;
import com.gigie.domain.SetmealDish;
import com.gigie.mapper.SetmealMapper;
import com.gigie.service.SetmealService;
import com.gigie.service.setmealdishservice;

import com.gigie.utils.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private setmealdishservice setmealdishService;
    @Override @Transactional  //保存套餐和套餐关联菜品
    public void savemeal_dish(SetmealDto setmealDto) {
        if (!this.save(setmealDto))
        {
           throw new CustomException("新增套餐失败");
        }
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long id=setmealDto.getId();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }

        if(!setmealdishService.saveBatch(setmealDishes))
        {
            throw new CustomException("新增套餐关联菜品失败");
        }

    }
    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @return
     */
    @Override
    public SetmealDto getDate(Long id) {
//        Setmeal setmeal = this.getById(id);
//        SetmealDto setmealDto = new SetmealDto();
//        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
//        //在关联表中查询，setmealdish
//        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);
//
//        if (setmeal != null){
//            BeanUtils.copyProperties(setmeal,setmealDto);
//            List<SetmealDish> list = setmealdishService.list(queryWrapper);
//            setmealDto.setSetmealDishes(list);
//            return setmealDto;
//        }
//        return null;










        Setmeal byId = this.getById(id);
        Long id1 = byId.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id1);
        queryWrapper.orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> list = setmealdishService.list(queryWrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(byId,setmealDto);
        setmealDto.setSetmealDishes(list);
        return setmealDto;


    }

    @Override
    @Transactional
    public void updatedish(SetmealDto setmealDto) {
        if(!this.updateById(setmealDto))
        {
            throw new CustomException("更新套餐失败");
        }
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish ::getSetmealId,setmealDto.getId());
        setmealdishService.remove(queryWrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long id=setmealDto.getId();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }

        if(!setmealdishService.saveBatch(setmealDishes))
        {
            throw new CustomException("更新套餐关联菜品失败");
        }

    }

    @Autowired
    private SetmealService SetmealService;
    @Override
    public void changestatus(int id, List<Long> ids) {


        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.set(Setmeal ::getStatus,id);
        queryWrapper.in(Setmeal ::getId,ids);
        boolean update = SetmealService.update(queryWrapper);
        if (!update) {
            throw new CustomException("停售套餐失败");
        }
    }
}
