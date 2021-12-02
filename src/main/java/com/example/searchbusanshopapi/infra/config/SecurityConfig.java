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
                .cors()
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);//세션사용을 막는다.

        http
                /**
                 * 이필터에서 로그인을 하게된다. 로직성공시
                 * security context에 저장, 이후 저장된 인증갑으로
                 * 필터마지막에 권한확인을 한후 실패시 Access Dined
                 */
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(),
                                jwtService,
                                refreshRepository),
                        UsernamePasswordAuthenticationFilter.class)
                //토큰유효성검사필터
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository, jwtService, redisService))
                .exceptionHandling()
                //필터체인에서 acceessIsDenied 예외를 던지면 이핸들러 호출
                .accessDeniedHandler(new CustomAccessDeniedHandler());
        http
                // 회원가입, 로그인요청 모두 허용
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/users", "/login")
                .permitAll()
                .and()
                // 모든 유저의 내용을 출력하는 요청, ADMIN으로 권한설정
                .authorizeRequests()
                .antMatchers( "/users")
                .hasRole("ADMIN")
                .and()
                //swagger 허용
                .authorizeRequests()
                .antMatchers( "/swagger*/**","/v3/api-docs")
                .permitAll()
                .and()
                //그외 모든요청은 인증필요(사이트마다 기획에 따라서 permitAll()로 변경가능)
                .authorizeRequests()
                .anyRequest().authenticated();

        http.logout().disable();
    }

    /**
     *      보안예외처리, 주로 resource(html, 정적파일..)등에 사용된다고 한다.
     *      아래처럼 짜는것은 잘못되기는 한듯...
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //기록 : webSecurity와의 차이점 공부해야함
        web.ignoring()
                .antMatchers("/jwt/authentication/refresh", "/logout");
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
