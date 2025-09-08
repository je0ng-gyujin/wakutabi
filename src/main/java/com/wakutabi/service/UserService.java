package com.wakutabi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wakutabi.domain.UserDto;
import com.wakutabi.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	
	
	public int countByusername(String username) {
		int result = userMapper.countByUsername(username);
		return result;
	}
	
	public void register(UserDto user) {
		String pw = passwordEncoder.encode(user.getPassword());
		
		user.setPassword(pw);
		
		userMapper.insertUser(user);
	}
}
