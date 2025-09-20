package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.LoginDto;
import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.UserPasswordUpdateDto;
import com.wakutabi.domain.UserUpdateDto;
@Mapper
public interface UserMapper {
		//username검색
		int countByUsername(@Param("username") String username);
		//회원가입처리
		int insertUser(SignUpDto user);
		//로그인 처리
		LoginDto findByUsername(String username);
		//회원정보 갖고오기
		UserUpdateDto getUserInfo(String username);
		//회원정보수정(유저 정보수정)
		void userInfoUpdate(UserUpdateDto user);
		//현재 비밀번호 가져오기
		String findPasswordByUsername(String username);
		//회원 비밀번호 수정
		void userPasswordUpdate(UserPasswordUpdateDto userPassword);
}
