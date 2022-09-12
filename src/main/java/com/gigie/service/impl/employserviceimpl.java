package com.gigie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gigie.domain.Employee;
import com.gigie.mapper.employmapper;
import com.gigie.service.employservice;
import org.springframework.context.annotation.Configuration;

@Configuration
public class employserviceimpl extends ServiceImpl<employmapper, Employee> implements employservice {

}
