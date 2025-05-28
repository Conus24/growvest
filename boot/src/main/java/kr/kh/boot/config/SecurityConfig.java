package kr.kh.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import kr.kh.boot.model.util.UserRole;
import kr.kh.boot.service.UserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

	@Autowired
	private UserDetailService memberDetailService;
	
	@Value("${security.rememberme.key}")
	private String rememberMeKey;

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf ->csrf.disable())
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/post/insert/*").hasAuthority(UserRole.USER.name())
                .requestMatchers("/admin/**").hasAnyAuthority(UserRole.ADMIN.name())
                .anyRequest().permitAll()  // 그 외 요청은 인증 필요
            )
            .formLogin((form) -> form
                //.loginPage("/login")  // 커스텀 로그인 페이지 설정
                .permitAll()           // 로그인 페이지는 접근 허용
                .loginProcessingUrl("/login")//
                .defaultSuccessUrl("/")
            )
            //자동 로그인 처리
						.rememberMe(rm-> rm
							.userDetailsService(memberDetailService)//자동 로그인할 때 사용할 userDetailService를 추가
							.key(rememberMeKey)//키가 변경되면 기존 토큰이 무효처리
							.rememberMeCookieName("LC")//쿠키 이름
							.tokenValiditySeconds(60 * 60 * 24 * 300)//유지 기간 : 300일
						)
			      .logout((logout) -> logout
            		.logoutUrl("/logout")
            		.logoutSuccessUrl("/")
            		.clearAuthentication(true)
            		.invalidateHttpSession(true)
            		.permitAll());  // 로그아웃도 모두 접근 가능
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
