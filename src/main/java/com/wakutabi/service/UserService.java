package com.wakutabi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.UserUpdateDto;
import com.wakutabi.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	
	public int countByUsername(String username) {
		int result = userMapper.countByUsername(username);
		return result;
	}
	
	public void register(SignUpDto user) {
		String pw = passwordEncoder.encode(user.getPassword());
		
		user.setPassword(pw);
		
		userMapper.insertUser(user);
	}

	public UserUpdateDto getUserInfo(String username){
		UserUpdateDto user = userMapper.getUserInfo(username);
		return user;
	}

	public void userInfoUpdate(UserUpdateDto user){
		userMapper.userInfoUpdate(user);
	}
}

