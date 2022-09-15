package com.gigie;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gigie.domain.Setmeal;
import com.gigie.mapper.SetmealMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GigieApplicationTests {
    @Autowired
    private SetmealMapper s;
    @Test
    void contextLoads() {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        Setmeal setmeal = s.selectById(1415580119015145474L);
        System.out.println(setmeal);
    }

}
