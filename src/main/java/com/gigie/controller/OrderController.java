package com.gigie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gigie.DTO.OrdersDto;
import com.gigie.DTO.SetmealDto;
import com.gigie.domain.*;
import com.gigie.service.*;
import com.gigie.utils.BaseContext;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService OrderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order)

    {
        log.info("订单数据{}",order);
        orderService.submit(order);
        return R.success("订单提交成功");
    }

    @GetMapping("/page")
    public R<Page<Orders>> getPage(int page, int pageSize, String number, String beginTime, String endTime)
    {
        log.info("{}",page);
        log.info("number:{}",number);
        log.info(beginTime);
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        queryWrapper.gt(StringUtils.isNotEmpty(beginTime),Orders ::getOrderTime,beginTime);
        queryWrapper.lt(StringUtils.isNotEmpty(endTime),Orders ::getOrderTime,endTime);
        queryWrapper.orderByDesc(Orders ::getOrderTime);
        orderService.page(pageInfo,queryWrapper);
        return  R.success(pageInfo);
    }


    @GetMapping("/userPage")
    public R<Page<OrdersDto>>   getorderPage(int page,int pageSize)
    {
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Long currentId = BaseContext.getCurrentId();
        log.info("currentid{}",currentId);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders ::getUserId,currentId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, queryWrapper);
        Page<OrdersDto> pageDto=new Page<>();
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<OrdersDto> list1 = new ArrayList<>();
        List<Orders> records = pageInfo.getRecords();
        for (Orders record : records) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(record, ordersDto);
            LambdaQueryWrapper<OrderDetail> queryWrap=new LambdaQueryWrapper<>();
            queryWrap.eq(OrderDetail ::getOrderId,record.getId());
            List<OrderDetail> list = OrderDetailService.list(queryWrap);
            if (list!=null)
            ordersDto.setOrderDetails(list);
            list1.add(ordersDto);

        }
            pageDto.setRecords(list1);
        return R.success(pageDto);


    }

    // TODO: 2022/9/17   再来一单
        @PostMapping("/again")
        public R<String>  again  (@RequestBody Map map)
        {
            String id =(String) map.get("id");
            LambdaQueryWrapper<OrderDetail>  queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail ::getOrderId,id);
            List<OrderDetail> list = OrderDetailService.list(queryWrapper);

            // 清空当前购物车
            shoppingCartService.clean();

            Long userId=BaseContext.getCurrentId();
            List<ShoppingCart> listShoppingCart=new ArrayList<>();
            for (OrderDetail item : list) {
                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.setUserId(userId);
                shoppingCart.setImage(item.getImage());
                Long dishId = item.getDishId();
                Long setmealId = item.getSetmealId();
                shoppingCart.setDishId(dishId);
                shoppingCart.setSetmealId(setmealId);
                shoppingCart.setName(item.getName());
                shoppingCart.setDishFlavor(item.getDishFlavor());
                shoppingCart.setNumber(item.getNumber());
                shoppingCart.setAmount(item.getAmount());
                shoppingCart.setCreateTime(LocalDateTime.now());
                listShoppingCart.add(shoppingCart);

            }

            shoppingCartService.saveBatch(listShoppingCart);

            return R.success("订购成功");
        }

















    // TODO: 2022/9/17  派送订单
    @PutMapping
    public R<String> orderStatusChange(@RequestBody Map<String,String> map){

        String id = map.get("id");
        String status1 = map.get("status");
        if (StringUtils.isEmpty(id)||StringUtils.isEmpty(status1)) {
            return R.error("传入信息不合法");

        }
        Long orderId = Long.parseLong(id);
        Integer status = Integer.parseInt(status1);
        Orders orders = orderService.getById(orderId);
        orders.setStatus(status);
        orderService.updateById(orders);

        return R.success("订单状态修改成功");

    }


}
