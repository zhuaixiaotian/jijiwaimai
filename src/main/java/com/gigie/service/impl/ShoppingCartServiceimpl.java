package com.gigie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gigie.domain.ShoppingCart;
import com.gigie.mapper.ShoppingcartMapper;
import com.gigie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceimpl extends ServiceImpl<ShoppingcartMapper, ShoppingCart> implements ShoppingCartService {
}
