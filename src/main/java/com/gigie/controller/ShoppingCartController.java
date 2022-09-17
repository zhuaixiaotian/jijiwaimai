package com.gigie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gigie.domain.ShoppingCart;
import com.gigie.service.ShoppingCartService;
import com.gigie.utils.BaseContext;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> addCart(@RequestBody ShoppingCart cart)
    {
        log.info("购物车数据{}",cart);

        //指定是那个购物车的数据
        Long currentId = BaseContext.getCurrentId();
        cart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart ::getUserId,currentId);
        //添加为菜品
        if (cart.getDishId()!=null)

        {
            queryWrapper.eq(ShoppingCart::getDishId,cart.getDishId());

        }
        else

        {
            queryWrapper.eq(ShoppingCart ::getSetmealId,cart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one == null)
        {   cart.setCreateTime(LocalDateTime.now());
            cart.setNumber(1);
            shoppingCartService.save(cart);
            one=cart;
        }
        else
        {
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);

        }
        return R.success(one);

    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> cartlist()
    {
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart ::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart ::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);

    }
    @GetMapping("/clean")
     public  R<String> clean()
    {
       shoppingCartService.clean();
        return R.success("清空购物车成功");


    }

    @PostMapping("sub")
    @Transactional
    public R<ShoppingCart> delete(@RequestBody ShoppingCart cart)

    {
        log.info("dishid{}",cart);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if (cart.getDishId()!=null)
        {
            queryWrapper.eq(ShoppingCart ::getDishId,cart.getDishId());
        }
        else
        {
            queryWrapper.eq(ShoppingCart ::getSetmealId,cart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one == null)
        {
            return R.error("购物车不存在");
        }

        Integer number = one.getNumber();
        if (number==1)
        {
            shoppingCartService.removeById(one);
        }
        else if (number>1)
        {
            one.setNumber(number-1);
            shoppingCartService.updateById(one);
        }
        else
        {
            return R.error("购物车不存在");
        }


        return R.success(one);
    }

}

