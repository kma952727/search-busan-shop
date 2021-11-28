package com.example.searchbusanshopapi.infra.config;

import com.example.searchbusanshopapi.infra.exception.handler.TokenCheckHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenCheckHandler())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger*/**");
    }
}
