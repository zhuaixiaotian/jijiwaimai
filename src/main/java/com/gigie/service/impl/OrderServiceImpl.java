package com.gigie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gigie.domain.*;
import com.gigie.mapper.OrderMapper;
import com.gigie.service.*;
import com.gigie.utils.BaseContext;
import com.gigie.utils.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得当前用户id
        //查询当前用户购物车数据
        //向订单插入一条数据
        //向订单明细插入多条数据
        //清空购物车

        //获得当前用户id
        Long currentId = BaseContext.getCurrentId();
        log.info("currentid{}",currentId);
        //查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> query = new LambdaQueryWrapper<>();
        query.eq(ShoppingCart ::getUserId,currentId);
        List<ShoppingCart> list = shoppingCartService.list(query);
        if (list==null || list.size() == 0)
        {
            throw new CustomException("购物车为空，无法下单");
        }

        User user = userService.getById(currentId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null)
        {
            throw new CustomException("地址为空，无法下单");
        }
        //订单号
        long id = IdWorker.getId();

        //向订单明细插入多条数据
        List<OrderDetail> od=new ArrayList<>();
        AtomicInteger amount = new AtomicInteger(0);

        for (ShoppingCart shoppingCart : list) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setAmount(shoppingCart.getAmount());
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(
                    shoppingCart.getNumber())).intValue());

            od.add(orderDetail);


        }
            orderDetailService.saveBatch(od);




        //向订单插入一条数据

        orders.setId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(id));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);


        //清空购物车
        shoppingCartService.remove(query);




    }
}
