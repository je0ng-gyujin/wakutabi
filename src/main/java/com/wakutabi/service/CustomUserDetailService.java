package com.wakutabi.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wakutabi.domain.UserDto;
import com.wakutabi.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService  {

	private final UserMapper userMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDto dto = userMapper.findByUsername(username);
		if(dto==null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없다"+username);
		}
			return User.builder()
					.username(dto.getUsername())
					.password(dto.getPassword())
					.roles(dto.getRole().name())
					.build();    
	}
}
