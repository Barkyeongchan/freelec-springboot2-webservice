package com.jojoldu.book.springboot.config.auth;

import com.jojoldu.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration  // @Configuration 어노테이션 추가 필요
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 비활성화 부분
                .csrf(csrf -> csrf.disable())  // 람다 방식으로 변경
                .headers(headers ->
                        headers.frameOptions(frameOptions -> frameOptions.disable())  // h2-console 화면을 사용하기 위해
                )

                // 권한 부여 부분
                .authorizeHttpRequests(auth -> auth  // authorizeRequests() → authorizeHttpRequests()
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll()  // antMatchers() → requestMatchers()
                        .requestMatchers("/api/v1/**").hasRole(Role.USER.name())  // 유저 네임이 있는 사용자만
                        .anyRequest().authenticated()
                )

                // 경로
                .logout(logout ->
                        logout.logoutSuccessUrl("/")  // 람다 방식으로 변경
                )

                // 로그인을 oauth로 한다
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)  // 람다 방식으로 변경
                        )
                );

        return http.build();  // SecurityFilterChain 반환
    }
}