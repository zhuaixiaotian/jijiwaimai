package com.gigie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gigie.domain.Setmeal;
import com.gigie.domain.SetmealDish;
import com.gigie.mapper.setmealdishmapper;
import com.gigie.service.SetmealService;
import com.gigie.service.setmealdishservice;
import com.gigie.utils.CustomException;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class setmealdishserviceimpl extends ServiceImpl<setmealdishmapper, SetmealDish> implements setmealdishservice {

}
