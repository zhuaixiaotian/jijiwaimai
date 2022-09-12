package com.gigie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gigie.domain.Employee;
import com.gigie.domain.User;
import com.gigie.mapper.employmapper;
import com.gigie.service.employservice;
import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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


    @PostMapping
    public R<Void> addnew(HttpSession session,@RequestBody Employee employe )
    {
        log.info(employe.toString());

//        String username=employe.getUsername();
//        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(Employee::getUsername, employe.getUsername());
//        Employee emp = employservice.getOne(queryWrapper);
//        if (emp!=null){
//            return R.error("用户已被注册");
//        }

        String passwd= DigestUtils.md5DigestAsHex("123".getBytes());
        employe.setPassword(passwd);
        employe.setCreateTime(LocalDateTime.now());
        employe.setUpdateTime(LocalDateTime.now());
        Long id = (Long)session.getAttribute("employee");
        employe.setCreateUser(id);
        employe.setUpdateUser(id);
        if (!employservice.save(employe))
        {
            return R.error("用户添加失败");
        }

        return  R.success(null);

    }
    @GetMapping("/page")
    public  R<IPage<Employee>> page(int page,int pageSize,String name)
    {

        System.out.println(""+page+"      "+pageSize+"         "+name);

        IPage<Employee> pageinfo=new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee ::getName,name);
        queryWrapper.orderByDesc(Employee ::getUpdateTime);

        employservice.page(pageinfo,queryWrapper);
//        if(page>pageinfo.getPages())
//        {
//            long pages = pageinfo.getPages();
//            pageinfo=employservice.page(new Page<>(pages,pageSize),queryWrapper);
//        }
        System.out.println("ok");
        return  R.success(pageinfo);
    }

    @PutMapping
    public  R<Void> update(@RequestBody Employee employe)
    {
            employe.setUpdateUser(1l);
            employe.setUpdateTime(LocalDateTime.now());
        boolean b = employservice.updateById(employe);
        if (!b)
        {
            throw new RuntimeException("修改失败");
        }
        return  R.success(null);

    }



    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id)
    {
        log.info("根据id查询员工信息");
        Employee byId = employservice.getById(id);
        if (byId == null)
        {
            return R.error("用户不存在");
        }

        return R.success(byId);


    }









}
