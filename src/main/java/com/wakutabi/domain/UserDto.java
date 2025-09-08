package com.wakutabi.domain;

public class UserDto {
	username   VARCHAR(20)                          NOT NULL UNIQUE,                      -- 사용자ID
    nickname   VARCHAR(20)                          NOT NULL UNIQUE,                      -- 닉네임(미입력 시 사용자ID)
    password   VARCHAR(256)                         NOT NULL,                             -- 비밀번호
    gender     ENUM('MALE','FEMALE','OTHER','NONE')          DEFAULT 'NONE',              -- 성별
    birth      DATETIME                             NOT NULL,                             -- 생일
    email      VARCHAR(255)                         NOT NULL UNIQUE,                      -- 이메일
    introduce  TEXT,                                                                      -- 자기소개글
    updated_at DATETIME
    
    
    
    user_id	   BIGINT	NOT NULL,	                -- 사용자ID
	city	   BOOLEAN	            DEFAULT FALSE,  -- 도시선호타입
	country	   BOOLEAN	            DEFAULT FALSE,  -- 시골선호타입
	eating	   BOOLEAN	            DEFAULT FALSE,  -- 식도락선호타입
	hot_spring BOOLEAN	            DEFAULT FALSE,  -- 온천선호타입
	otaku	   BOOLEAN	      
}
