package com.example.userservice.security;

import com.example.userservice.service.UserService;
import javax.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //인증에 관련된 설정
        auth.userDetailsService(userService)
        .passwordEncoder(bCryptPasswordEncoder);//변환작업
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() //CSRF 필터 사용 X
                //.authorizeRequests().antMatchers("/users/**").permitAll() //users는 모두 통과
                .authorizeRequests().antMatchers("/**")//모든 요청에
                //.hasIpAddress("192.168.0.5") //해당 IP만 권한 체크
                .permitAll()
                .and()
                .addFilter(getAuthenticationFilter()) //필터 추가
        ;
        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(),userService, env);
        //authenticationFilter.setAuthenticationManager(authenticationManager()); //스프링 시큐리티에서 가지고온 manager
        return authenticationFilter;
    }
}
