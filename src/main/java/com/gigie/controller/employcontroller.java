package com.gigie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gigie.domain.Employee;
import com.gigie.domain.User;
import com.gigie.mapper.employmapper;
import com.gigie.service.employservice;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/employee")
public class employcontroller {
    @Autowired
    private employservice employservice;


    @PostMapping("/login")
    public R<Employee>  login(HttpSession session,@RequestBody Employee employe)
    {
        String passwd=employe.getPassword();
        passwd= DigestUtils.md5DigestAsHex(passwd.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
         queryWrapper.eq(Employee::getUsername, employe.getUsername());
        Employee emp = employservice.getOne(queryWrapper);
        if (emp == null)
        {
            return R.error("用户不存在");

        }
        if (!passwd.equals(emp.getPassword()))
        {
            return R.error("密码错误");
        }
        if (emp.getStatus() == 0)
        {
            return  R.error("账号已禁用");
        }
        session.setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    @PostMapping("/logout")
    public R<Void> logout(HttpSession session)
    {
        session.removeAttribute("employee");
        return R.success(null);
    }

//    @PostMapping("/register")
//    public R<Void> register(HttpSession session,@RequestBody Employee employe)
//    {
//        String username=employe.getUsername();
//        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(Employee::getUsername, employe.getUsername());
//        Employee emp = employservice.getOne(queryWrapper);
//        if (emp!=null){
//            return R.error("用户已被注册");
//        }
//        String passwd=employe.getPassword();
//        passwd= DigestUtils.md5DigestAsHex(passwd.getBytes());
//        employe.setPassword(passwd);
//        Long id =(Long) session.getAttribute("employee");
//        employe.setCreateUser((id==null?0l:id));
//        employe.setCreateTime(LocalDateTime.now());
//        employe.setName("");
//
//        Integer insert = usermapper.insert(user);
//        if (insert!=1){
//            throw  new InsertException("注册产生错误"); //5000
//        }
//
//
//    }
}
