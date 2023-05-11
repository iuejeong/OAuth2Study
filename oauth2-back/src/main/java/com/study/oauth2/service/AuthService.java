package com.study.oauth2.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.study.oauth2.dto.auth.OAuth2ProviderMergeReqDto;
import com.study.oauth2.dto.auth.OAuth2RegisterReqDto;
import com.study.oauth2.entity.Authority;
import com.study.oauth2.entity.User;
import com.study.oauth2.repository.UserRepository;
import com.study.oauth2.security.OAuth2Attribute;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{

	private final UserRepository userRepository;
	
	// userRequest에 google에서 들고온 user 정보가 담겨져있음
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);	// oAuth2User에는 방금 로그인한 user 정보가 들어가있다.
		
		System.out.println(oAuth2User);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();	// providerName(google, naver, kakao)
		
		// oAuth2User.getAttributes()는 User Attrivute 내용을 map으로 모두 전달함.
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, oAuth2User.getAttributes());
		
		Map<String, Object> attributes = oAuth2Attribute.convertToMap();
		
		// 아래 코드는 Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")을 풀어서 쓴 것
//		ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
//		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		// 권한 설정, 통일 시킨 attribute 정보를 map의 상태로, key값(email or 아이디 상관 없음)
		// return이 되면 인증이 성공이 되는 것
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes, "email");
	}
	
	public int oauth2register(OAuth2RegisterReqDto oAuth2RegisterReqDto) {
		User userEntity = oAuth2RegisterReqDto.toEntity();
		
		userRepository.saveUser(userEntity);
		return userRepository.saveAuthority(
				Authority.builder()
				.userId(userEntity.getUserId())
				.roleId(1)
				.build()
			);
	}
	
	public boolean checkPassword(String email, String password) {
		User userEntity = userRepository.findUserByEmail(email);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		return passwordEncoder.matches(password, userEntity.getPassword());
	}
	
	public int oAuth2ProviderMerge(OAuth2ProviderMergeReqDto oAuth2ProviderMergeReqDto) {
		User userEntity = userRepository.findUserByEmail(oAuth2ProviderMergeReqDto.getEmail());
		
		String provider = oAuth2ProviderMergeReqDto.getProvider();
		
		if(StringUtils.hasText(userEntity.getProvider())) {
			userEntity.setProvider(userEntity.getProvider() + "," + provider);	// 구글, 네이버 등 있을 때 ex) provider 컬럼에 google, naver가 들어감
		}else {
			userEntity.setProvider(provider);	// provider가 아예 없을 때
		}
		
		return userRepository.updateProvider(userEntity);
	}
	
}





















