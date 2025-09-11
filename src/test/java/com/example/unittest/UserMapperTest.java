package com.example.unittest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.SignUpDto.Role;
import com.wakutabi.mapper.UserMapper;

@SpringBootTest
@MapperScan("com.wakutabi.mapper")
public class UserMapperTest {

	@Autowired
	UserMapper userMapper;
	
	@Test
	public void insertUser() {
		
		SignUpDto test = SignUpDto.builder()
		.username("test")
		.nickname("hello")
		.password("1234")
		.birth(LocalDate.now())
		.email("test@google.com")
		.createdAt(LocalDateTime.now())
		.role(Role.USER)
		.build();
		
		
		userMapper.insertUser(test);
	}
	
}
