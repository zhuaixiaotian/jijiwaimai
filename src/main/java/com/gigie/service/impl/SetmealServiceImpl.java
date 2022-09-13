package com.gigie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gigie.domain.Setmeal;
import com.gigie.mapper.SetmealMapper;
import com.gigie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
