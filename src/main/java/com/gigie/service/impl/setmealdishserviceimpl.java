package com.gigie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gigie.domain.SetmealDish;
import com.gigie.mapper.setmealdishmapper;
import com.gigie.service.setmealdishservice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class setmealdishserviceimpl extends ServiceImpl<setmealdishmapper, SetmealDish> implements setmealdishservice {
}
