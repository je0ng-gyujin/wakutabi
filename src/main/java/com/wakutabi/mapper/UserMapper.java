package com.wakutabi.mapper;

import com.wakutabi.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
		// username검색
		int countByUsername(@Param("username") String username);
		// 회원가입처리
		int insertUser(SignUpDto user);
		// 로그인 처리
		LoginDto findByUsername(String username);
		// 유저id 가져오기
		Long getUserId(String username);
		// 회원정보 갖고오기
		UserUpdateDto getUserInfo(String username);
		// 회원정보수정(유저 정보수정)
		void userInfoUpdate(UserUpdateDto user);
		// 현재 비밀번호 가져오기
		String findPasswordByUsername(String username);
		// 회원 비밀번호 수정
		void userPasswordUpdate(UserPasswordUpdateDto userPassword);
		// 회원 탈퇴(탈퇴이유 생기면 값 추가예정)
		void userWithdrawal(String username);

}
