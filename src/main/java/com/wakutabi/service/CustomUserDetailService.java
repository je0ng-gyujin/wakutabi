package com.wakutabi.service;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wakutabi.domain.LoginDto;
import com.wakutabi.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService  {

	private final UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LoginDto dto = userMapper.findByUsername(username);
		System.out.println(dto.getIsVerified());

		if(dto==null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없다"+username);
		}
		if("EXIT".equalsIgnoreCase(dto.getStatus())){
			System.out.println("status 값: " + dto.getStatus());
			throw new DisabledException("탈퇴한 계정입니다.");
		}
		if(!dto.getIsVerified()){
			throw  new DisabledException("이메일 인증이 완료되지 않은 계정입니다.");
		}
			return User.builder()
					.username(dto.getUsername())
					.password(dto.getPassword())
					.disabled(!dto.getIsVerified())
					.build();    
	}
}
