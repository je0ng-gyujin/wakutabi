-- 이용자 관련
CREATE TABLE USERS(
    id BIGINT NOT NULL PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    nickname VARCHAR(20) NOT NULL,
    password VARCHAR(256) NOT NULL,
    gender ENUM('MALE','FEMALE','OTHER','NONE') DEFAULT 'NONE',
    birth DATETIME,
    email VARCHAR(255) NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(5) NOT NULL DEFAULT 'USER',
    statue ENUM('ACTIVE','SUSPENDED','BANNED') NOT NULL DEFAULT 'ACTIVE',
    introduce TEXT,
    created_at DATETIME NOT NULL DEFAULT 'CURRENT_TIMESTAMP',
    updated_at DATETIME DEFAULT 'CURRENT_TIMESTAMP'
);
-- 관리자 관련

-- 여행등록 관련

-- 여행후기 관련

-- 고객센터 관련