package com.gigie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import javax.servlet.MultipartConfigElement;

@Slf4j
@MapperScan("com.gigie.mapper")
@SpringBootApplication
public class GigieApplication {

    public static void main(String[] args) {
        SpringApplication.run(GigieApplication.class, args);
    }

    @Bean
    public MultipartConfigElement getMultipartConfigElement ()
    {
        MultipartConfigFactory factory=new MultipartConfigFactory();

        factory.setMaxFileSize(DataSize.of(10, DataUnit.MEGABYTES) );
        factory.setMaxRequestSize(DataSize.of(15,DataUnit.MEGABYTES));

        return  factory.createMultipartConfig();
    }

}
