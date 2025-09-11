package com.wakutabi.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.SignUpDto.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void countByUsername() {
    }

    @Test
    void insertUser() {
        SignUpDto test = SignUpDto.builder()
                .username("wakutabi")
                .password("wakutabi")
                .birth(LocalDate.now())
                .email("wakutabi@wakutabi.com")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .nickname("wakutabi")
                .build();

        userMapper.insertUser(test);

    }

    @Test
    void findByUsername() {
    }
}