package com.gigie.interceptor;

import com.alibaba.fastjson.JSON;
import com.gigie.utils.BaseContext;
import com.gigie.utils.R;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class loginintercepton implements HandlerInterceptor {

    /**
     *检测全局session对象中是否有uid数据,如果有则放行,如果没有重定向到登录页面
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器(把url和Controller映射到一块)
     * @return 返回值为true放行当前请求,反之拦截当前请求
     * @throws Exception
     */
    @Override
    //在DispatcherServlet调用所有处理请求的方法前被自动调用执行的方法
    //springboot会自动把请求对象给到request,响应对象给到response,适配器给到handler
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {


//        通过HttpServletRequest对象来获取session对象
        Object obj = request.getSession().getAttribute("employee");
        if (obj != null) {
            Long empid=(Long) obj;
            BaseContext.setCurrentId(empid);
//
//            response.sendRedirect("/backend/page/login/login.html");
            //停止后续的调用
            return true;
        }

        Object obj2 = request.getSession().getAttribute("user");

        if (obj2 != null) { //说明用户没有登录过系统,则重定向到login.html页面
            //不能用相对路径,因为这里是要告诉前端访问的新页面是在哪个目录下的新
            //页面,但前面的localhost:8080可以省略,因为在同一个项目下
            Long userid=(Long) obj2;
            BaseContext.setCurrentId(userid);
//            response.sendRedirect("/front/page/login.html");
            //结束后续的调用

            return true;
        }
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;
    }


    }












//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//
//    }

