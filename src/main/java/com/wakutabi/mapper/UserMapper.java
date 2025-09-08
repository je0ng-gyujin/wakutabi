package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wakutabi.domain.UserDto;
@Mapper
public interface UserMapper {
		//username검색
		int countByUsername(String username);
		//회원가입처리
		int insertUser(UserDto user);
		//로그인 처리
		UserDto findByUsername(String username);
}
