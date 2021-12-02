package com.example.searchbusanshopapi.infra.config;

import com.example.searchbusanshopapi.infra.exception.handler.TokenCheckHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //모든요청이 필터체인이후 거치게 됨
        registry.addInterceptor(new TokenCheckHandler())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger*/**");
    }

}
