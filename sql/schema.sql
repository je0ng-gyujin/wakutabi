-- [사용자 관련]
-- 사용자
CREATE TABLE users (
    id         BIGINT                               AUTO_INCREMENT PRIMARY KEY,                                     -- 고유ID
    username   VARCHAR(20)                          NOT NULL UNIQUE,                                                -- 사용자ID
    nickname   VARCHAR(20)                          NOT NULL UNIQUE,                                                -- 닉네임(미입력 시 사용자ID)
    password   VARCHAR(256)                         NOT NULL,                                                       -- 비밀번호
    gender     ENUM('MALE','FEMALE','OTHER','NONE') NOT NULL DEFAULT 'NONE',                                        -- 성별
    birth      DATETIME                             NOT NULL,                                                       -- 생일
    email      VARCHAR(255)                         NOT NULL UNIQUE,                                                -- 이메일
    is_public  BOOLEAN                              NOT NULL DEFAULT TRUE,                                          -- 공개여부(기본 공개)
    role       ENUM('USER','ADMIN')                 NOT NULL DEFAULT 'USER',                                        -- 사용자권한('USER': 일반사용자, 'ADMIN': 관리자)
    status     ENUM('ACTIVE','SUSPENDED','BANNED')  NOT NULL DEFAULT 'ACTIVE',                                      -- 계정상태('ACTIVE':활성화, 'SUSPEND':일시정지, 'BANNED':영구정지)
    introduce  TEXT,                                                                                                -- 자기소개글
    created_at DATETIME                             NOT NULL DEFAULT CURRENT_TIMESTAMP,                             -- 생성일자
    updated_at DATETIME                                      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일자
    deleted_at DATETIME                                                                                             -- 삭제(탈퇴)일자
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
-- [여행등록 관련]
-- 여행일정 등록
CREATE TABLE trip_article (
	id	               BIGINT                                         AUTO_INCREMENT PRIMARY KEY,                                             -- 여행일정게시 고유ID
	host_user_id	   BIGINT                                         NOT NULL,	                                                              -- 주최자 고유ID
	location           VARCHAR(10)   	                              NOT NULL,	                                                              -- 지역
	start_date	       DATETIME	                                      NOT NULL,                                                               -- 여행시작날짜
	end_date	       DATETIME	                                      NOT NULL,                                                               -- 여행종료날짜
	maxParticipants    INT                                            NOT NULL DEFAULT 10,                                                    -- 최대인원수:10명
	age_limit	       CHAR(2)                                        NOT NULL CHECK(AGE_LIMIT IN('NO','20','30','40','MX')),                 -- 나이제한('NO':상관없음,'20':20대,'30':30대,'40':40대,'MX':혼합)
	gender_limit	   CHAR(1)                                        NOT NULL CHECK(GENDER_LIMIT IN('N','M','F')),                           -- 성별제한('N':상관없음,'M':남자,'F':여자)
	title	           VARCHAR(50)                                    NOT NULL,                                                               -- 제목
	content            TEXT                                           NOT NULL,                                                               -- 내용
	estimated_cost     INT                                            NOT NULL DEFAULT 0,                                                     -- 예상비용
	status	           ENUM('OPEN','MATCHED','CLOSED','CANCELED')     NOT NULL DEFAULT 'OPEN',                                                -- 진행상태('OPEN':진행중,'MATCHED':매치완료,'CLOSED':매치종료,'CANCELED':매치취소)
    created_at         DATETIME                                       NOT NULL DEFAULT CURRENT_TIMESTAMP,                                     -- 생성일자
    updated_at         DATETIME                                                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,         -- 수정일자
    deleted_at         DATETIME,                                                                                                              -- 삭제일자

	CONSTRAINT fk_trip_article_host_user_id FOREIGN KEY (host_user_id) REFERENCES users (id)
);
-- 여행등록 이미지
CREATE TABLE trip_article_image (
	id	                   BIGINT          AUTO_INCREMENT PRIMARY KEY,   -- 여행등록 이미지 고유ID
	trip_article_id	       BIGINT	       NOT NULL,                     -- 여행등록ID
	image_path             VARCHAR(255),                                 -- 이미지
	image_path_content	   TEXT,                                         -- 이미지내용
	is_main	               BOOLEAN,                                      -- 메인이미지 여부
	order_number           INT,                                          -- 이미지 표시 순서

	CONSTRAINT fk_trip_article_image_trip_article_id FOREIGN KEY (trip_article_id) REFERENCES trip_article (id)
);

-- [여행후기 관련]
-- 사용자 간 리뷰
CREATE TABLE user_by_user_review (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY, -- 사용자 간 리뷰 고유ID
    user_id              BIGINT NOT NULL,                   -- 주최자를 리뷰하는 유저 고유ID
    reviewed_user_id     BIGINT NOT NULL,                   -- 리뷰를 받는 주최자 고유ID
    rating               INT    NOT NULL,                   -- 별점

    CONSTRAINT fk_user_by_user_review_user_id              FOREIGN KEY (user_id)              REFERENCES users (id),
    CONSTRAINT fk_user_by_user_review_reviewed_user_id     FOREIGN KEY (reviewed_user_id)     REFERENCES users (id)
);
-- 고객센터 관련