package com.gigie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gigie.DTO.SetmealDto;
import com.gigie.domain.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    void savemeal_dish(SetmealDto setmealDto);
    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @return
     */
    SetmealDto getDate(Long id);

    void updatedish(SetmealDto setmealDto);
}
