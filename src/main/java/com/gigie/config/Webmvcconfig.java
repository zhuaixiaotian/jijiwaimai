package com.gigie.config;


import com.gigie.interceptor.loginintercepton;
import com.gigie.utils.JacksonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**拦截器的注册*/
@Configuration //自动加载当前的类并进行拦截器的注册,如果没有@Configuration就相当于没有写类LoginInterceptorConfigure
public class Webmvcconfig implements WebMvcConfigurer {
    @Autowired
    private loginintercepton loginintercepton;

    @Override
    //配置拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        //1.创建自定义的拦截器对象

        //2.配置白名单并存放在一个List集合
        List<String> patterns = new ArrayList<>();
        patterns.add("/backend/api/**");
        patterns.add("/backend/images/**");
        patterns.add("/backend/js/**");
        patterns.add("/backend/plugins/**");
        patterns.add("/backend/styles/**");

        patterns.add("/backend/page/login/login.html");
        patterns.add("/employee/login");
        patterns.add("/front/**");

        //registry.addInterceptor(interceptor);完成拦截
        // 器的注册,后面的addPathPatterns表示拦截哪些url
        //这里的参数/**表示所有请求,再后面的excludePathPatterns表
        // 示有哪些是白名单,且参数是列表
       registry.addInterceptor(loginintercepton).addPathPatterns
                     ("/**")
               .excludePathPatterns(patterns);

    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
            converters.add(0,messageConverter);





    }
}
