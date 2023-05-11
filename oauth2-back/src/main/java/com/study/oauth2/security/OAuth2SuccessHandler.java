package com.study.oauth2.security;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.study.oauth2.entity.User;
import com.study.oauth2.repository.UserRepository;
import com.study.oauth2.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");
		String provider = oAuth2User.getAttribute("provider");
		User userEntity = userRepository.findUserByEmail(email);
		System.out.println(provider);
		if(userEntity == null) {
			String registerToken = jwtTokenProvider.generateOAuth2RegisterToken(authentication);
			String name = oAuth2User.getAttribute("name");
			System.out.println(name);
			response
			.sendRedirect(
					"http://localhost:3000/auth/oauth2/register"
							+ "?registerToken=" + registerToken
							+ "&email=" + email
							+ "&name=" + URLEncoder.encode(name, "UTF-8")
							+ "&provider=" + provider
			);
		}else {
			if(StringUtils.hasText(userEntity.getProvider())) {
				// 회원가입이 되어 있고, provider가 등록된 경우
				if(!userEntity.getProvider().contains(provider)) {
					// 하지만 로그인된 oauth2 계정의 provider가 null 경우
					response.sendRedirect(
							"http://localhost:3000/auth/oauth2/merge"
					 			 	+ "?provider=" + provider
									+ "&email=" + email);
					return;	// return을 해주지 않으면 위랑 밑에 login도 실행이 됨.
				}
				
				response.sendRedirect("http://localhost:3000/auth/oauth2/login"
				 			 	+ "?accessToken=" + jwtTokenProvider.generateAccessToken(authentication));
				
			}else {
				// 회원가입은 되어 있지만 provider가 null인 경우(계정 통합)
				response.sendRedirect(
						"http://localhost:3000/auth/oauth2/merge"
				 			 	+ "?provider=" + provider
								+ "&email=" + email);
			}
		}
		
	}
	
}
