package com.gigie.controller;

import com.gigie.DTO.DishDto;
import com.gigie.service.DishFlavorservice;
import com.gigie.service.DishService;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private  DishFlavorservice dishFlavorservice;


    @PostMapping

    public R<Void> addnewdish(@RequestBody  DishDto dishDto)
    {

        dishService.saveflavor(dishDto);
        return R.success(null);
    }




}
