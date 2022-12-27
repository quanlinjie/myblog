package com.study.myblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.study.myblog.handler.LoginSuccessHandler;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)

public class SecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/s/**").authenticated()
		.anyRequest()
		.permitAll() // 로그인을 하지 않더라도 모든 페이지에 접근
		
		.and()
      .formLogin()
      .loginPage("/login-form")
      .loginProcessingUrl("/login")
      .successHandler(new LoginSuccessHandler());
  
		
		return http.build();
	}
	
	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() { // PasswordEncoder 빈(bean)을 만드는 가장 쉬운 방법
        return new BCryptPasswordEncoder();
    }
	
	@Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { 
        return authenticationConfiguration.getAuthenticationManager(); // AuthenticationManager는 스프링 시큐리티의 인증을 담당
        // UserSecurityService와 PasswordEncoder가 자동으로 설정
    }
}	
		

