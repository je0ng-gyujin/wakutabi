-- [사용자 관련]
-- 사용자
CREATE TABLE users (
    id         BIGINT                               AUTO_INCREMENT PRIMARY KEY,           -- 고유ID
    username   VARCHAR(20)                          NOT NULL UNIQUE,                      -- 사용자ID
    nickname   VARCHAR(20)                          NOT NULL UNIQUE,                      -- 닉네임(미입력 시 사용자ID)
    password   VARCHAR(256)                         NOT NULL,                             -- 비밀번호
    gender     ENUM('MALE','FEMALE','OTHER','NONE')          DEFAULT 'NONE',              -- 성별
    birth      DATETIME                             NOT NULL,                             -- 생일
    email      VARCHAR(255)                         NOT NULL UNIQUE,                      -- 이메일
    is_public  BOOLEAN                              NOT NULL DEFAULT TRUE,                -- 공개여부
    role       ENUM('USER','ADMIN')                 NOT NULL DEFAULT 'USER',              -- 사용자권한('USER': 일반사용자, 'ADMIN': 관리자)
    status     ENUM('ACTIVE','SUSPENDED','BANNED')  NOT NULL DEFAULT 'ACTIVE',            -- 계정상태('ACTIVE':활성화, 'SUSPEND':일시정지, 'BANNED':영구정지)
    introduce  TEXT                                     NULL DEFAULT NUll,                                                                      -- 자기소개글
    created_at DATETIME                             NOT NULL DEFAULT CURRENT_TIMESTAMP,   -- 생성일자
    updated_at DATETIME                                      DEFAULT CURRENT_TIMESTAMP    -- 수정일자
);
-- 사용자 선호도
CREATE TABLE user_prefer (
	id	       BIGINT   AUTO_INCREMENT PRIMARY KEY, -- 선호도 고유ID
	user_id	   BIGINT	NOT NULL,	                -- 사용자ID
	city	   BOOLEAN	            DEFAULT FALSE,  -- 도시선호타입
	country	   BOOLEAN	            DEFAULT FALSE,  -- 시골선호타입
	eating	   BOOLEAN	            DEFAULT FALSE,  -- 식도락선호타입
	hot_spring BOOLEAN	            DEFAULT FALSE,  -- 온천선호타입
	otaku	   BOOLEAN	            DEFAULT FALSE,  -- 오타쿠선호타입

	CONSTRAINT fk_user_prefer_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);
-- 여행등록 관련

-- 여행후기 관련

-- 고객센터 관련