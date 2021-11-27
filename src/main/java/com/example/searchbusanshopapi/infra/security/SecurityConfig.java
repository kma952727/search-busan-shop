package com.example.searchbusanshopapi.infra.security;

import com.example.searchbusanshopapi.infra.exception.handler.CustomAccessDenineHandler;
import com.example.searchbusanshopapi.infra.jwt.JwtAuthenticationFilter;
import com.example.searchbusanshopapi.infra.jwt.JwtAuthorizationFilter;
import com.example.searchbusanshopapi.infra.jwt.JwtService;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);//세션사용을 막는다.

        http
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), jwtService), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository, jwtService))
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDenineHandler());
        http
                .authorizeRequests()
                .antMatchers("/users", "/")
                .permitAll()
                .antMatchers("/shops")
                .access("hasRole('ROLE_USER')");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
