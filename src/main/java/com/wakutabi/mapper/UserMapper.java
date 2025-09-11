package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.UserDto;
@Mapper
public interface UserMapper {
		//username검색
		int countByUsername(@Param("username") String username);
		//회원가입처리
		int insertUser(UserDto user);
		//로그인 처리
		UserDto findByUsername(String username);
}
