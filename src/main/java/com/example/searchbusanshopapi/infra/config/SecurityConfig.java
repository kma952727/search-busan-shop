package com.example.searchbusanshopapi.infra.config;

import com.example.searchbusanshopapi.infra.exception.handler.CustomAccessDeniedHandler;
import com.example.searchbusanshopapi.infra.jwt.JwtAuthenticationFilter;
import com.example.searchbusanshopapi.infra.jwt.JwtAuthorizationFilter;
import com.example.searchbusanshopapi.infra.jwt.JwtService;
import com.example.searchbusanshopapi.infra.redis.RedisService;
import com.example.searchbusanshopapi.user.repository.RefreshRepository;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final RedisService redisService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);//세션사용을 막는다.

        http
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(),
                                jwtService,
                                refreshRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository, jwtService, redisService))
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler());
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/users", "/login")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers( "/users")
                .hasRole("ADMIN")
                .and()
                .authorizeRequests()
                .antMatchers( "/", "/swagger*/**","/v3/api-docs")
                .permitAll()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();

        http.logout().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/jwt/authentication/refresh", "/logout");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
