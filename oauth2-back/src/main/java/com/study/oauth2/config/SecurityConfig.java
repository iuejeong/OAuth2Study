package com.study.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;

import com.study.oauth2.security.OAuth2SuccessHandler;
import com.study.oauth2.service.AuthService;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final AuthService authService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable();
		http.formLogin().disable();
		http.cors();
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests()
			.antMatchers("/auth/**")
			.permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.oauth2Login()	// 서버로 보내는 순간 여기로 타고 들어옴
			.loginPage("http://localhost:3000/auth/login")
			// 로그인이 성공했을 때의 filter 하지만 우리는 jwt 토큰을 줘야 로그인이 되게 할 것이기 때문에 service에서 로그인이 됐다고 해서 성공을 한 건 아님.
			// 성공이 되면 Authentication에 저장이 됨.
			.successHandler(oAuth2SuccessHandler)
			// 로그인한 user에 대한 정보가 담겨져있음, 8080 -> google -> code발급(로그인을 하는 순간) -> google -> scope에 있는 token 등 user 정보를 가져옴
			.userInfoEndpoint()
			.userService(authService);		// userInfoEndpoint()이 동작을 하면 service로 넘어감
	}
	
}
