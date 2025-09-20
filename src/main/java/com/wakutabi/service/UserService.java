package com.wakutabi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.UserPasswordUpdateDto;
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
	// 회원정보가져오기
	public UserUpdateDto getUserInfo(String username){
		UserUpdateDto user = userMapper.getUserInfo(username);
		return user;
	}
	// 회원정보 수정
	public void userInfoUpdate(UserUpdateDto user){
		userMapper.userInfoUpdate(user);
	}
	// 현재 비밀번호 체크
	public boolean checkCurrentPassword(String username, String currentPassword){
		String encodedPassword = userMapper.findPasswordByUsername(username);
		if(encodedPassword == null){
			return false;
		}
		return passwordEncoder.matches(currentPassword, encodedPassword);
	}
	// 비밀번호 수정
	public void userPasswordUpdate(UserPasswordUpdateDto newPassword){
		String pw = passwordEncoder.encode(newPassword.getNewPassword());
		newPassword.setNewPassword(pw);

		userMapper.userPasswordUpdate(newPassword);
	}

}

