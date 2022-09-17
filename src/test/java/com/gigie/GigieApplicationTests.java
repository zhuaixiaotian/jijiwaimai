package com.gigie;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gigie.domain.Setmeal;
import com.gigie.mapper.SetmealMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class GigieApplicationTests {
    @Autowired
    private SetmealMapper s;
    @Test
    void contextLoads() {
        List<Integer> a= Arrays.asList(1,2,3);
        List<Integer> b = Arrays.asList(1,2,3);
        boolean flag = true;
        int i=0;
        for (Integer integer : b) {
            flag&=a.contains(integer);
            if (!flag)
                break;
            i++;
        }
        System.out.println(i);
        System.out.println(a.contains(b));
        System.out.println(a.containsAll(b));
    }



}
  abstract  class  a
{
    public a() {
        {

        }
    }
}
