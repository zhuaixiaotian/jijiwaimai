package com.gigie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gigie.domain.ShoppingCart;
import com.gigie.mapper.ShoppingcartMapper;
import com.gigie.service.ShoppingCartService;
import com.gigie.utils.BaseContext;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceimpl extends ServiceImpl<ShoppingcartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public void clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart ::getUserId, BaseContext.getCurrentId());
        this.remove(queryWrapper);
    }
}
