package com.bytedance.douyinclouddemo.config;

import com.bytedance.douyinclouddemo.filter.AppFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<AppFilter> appFilterFilterRegistrationBean() {
        FilterRegistrationBean<AppFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AppFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
