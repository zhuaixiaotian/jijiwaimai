package com.gigie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Slf4j
@MapperScan("com.gigie.mapper")
@SpringBootApplication
public class GigieApplication {

    public static void main(String[] args) {
        SpringApplication.run(GigieApplication.class, args);
    }

}
