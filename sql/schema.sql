-- [사용자 관련]
-- 사용자
CREATE TABLE users (
    id           BIGINT                                      AUTO_INCREMENT PRIMARY KEY,                                     -- 고유ID
    username     VARCHAR(20)                                 NOT NULL UNIQUE,                                                -- 사용자ID
    nickname     VARCHAR(20)                                 NOT NULL UNIQUE,                                                -- 닉네임(미입력 시 사용자ID)
    password     VARCHAR(256)                                NOT NULL,                                                       -- 비밀번호
    gender       ENUM('MALE','FEMALE','OTHER','NONE')        NOT NULL DEFAULT 'NONE',                                        -- 성별
    birth        DATE                                        NOT NULL,                                                       -- 생일
    email        VARCHAR(255)                                NOT NULL UNIQUE,                                                -- 이메일
    image_path   VARCHAR(255),                                                                                               -- 프로필사진
    is_public    BOOLEAN                                     NOT NULL DEFAULT TRUE,                                          -- 공개여부(기본 공개)
    role         ENUM('USER','ADMIN')                        NOT NULL DEFAULT 'USER',                                        -- 사용자권한('USER': 일반사용자, 'ADMIN': 관리자)
    status       ENUM('ACTIVE','SUSPENDED','BANNED','EXIT')  NOT NULL DEFAULT 'ACTIVE',                                      -- 계정상태('ACTIVE':활성화, 'SUSPENDED':일시정지, 'BANNED':영구정지)
    exit_reason  VARCHAR(500),                                                                                               -- 탈퇴이유
    introduce    TEXT,                                                                                                       -- 자기소개글
    rating       DOUBLE,                                                                                                     -- 평점
    created_at   DATETIME                                    NOT NULL DEFAULT CURRENT_TIMESTAMP,                             -- 생성일자
    updated_at   DATETIME                                             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 수정일자
);
-- [태그 관련]
-- 태그
CREATE TABLE trip_tag (
	id	          BIGINT       AUTO_INCREMENT PRIMARY KEY,  -- 트립태그 고유ID
	tag_name      VARCHAR(50)  NOT NULL UNIQUE              -- 태그 이름
);
-- 사용자 태그 중간테이블
CREATE TABLE trip_tag_mid_users (
    user_id      BIGINT   NOT NULL,         -- 사용자 고유ID
    trip_tag_id  BIGINT   NOT NULL,         -- 여행태그 고유ID

    PRIMARY KEY (user_id, trip_tag_id),
    CONSTRAINT fk_trip_tag_mid_users_user_id     FOREIGN KEY (user_id)     REFERENCES users    (id),
    CONSTRAINT fk_trip_tag_mid_users_trip_tag_id FOREIGN KEY (trip_tag_id) REFERENCES trip_tag (id)
);
-- 여행 태그 중간테이블
CREATE TABLE trip_tag_mid_trip_article (
    trip_tag_id       BIGINT   NOT NULL,         -- 여행태그 고유ID
    trip_article_id   BIGINT   NOT NULL,         -- 여행일정 고유ID

    PRIMARY KEY (trip_article_id, trip_tag_id),
    CONSTRAINT fk_trip_tag_mid_trip_article_trip_tag_id     FOREIGN KEY (trip_tag_id)     REFERENCES trip_tag     (id),
    CONSTRAINT fk_trip_tag_mid_trip_article_trip_article_id FOREIGN KEY (trip_article_id) REFERENCES trip_article (id)
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
	order_number           INT,                                          -- 이미지 표시 순서

	CONSTRAINT fk_trip_article_image_trip_article_id FOREIGN KEY (trip_article_id) REFERENCES trip_article (id)
);
-- [여행 참가]
-- 여행 참가 신청
CREATE TABLE trip_join_request (
    id                 BIGINT                                  AUTO_INCREMENT PRIMARY KEY,          -- 여행참가신청ID
    trip_article_id    BIGINT                                  NOT NULL,                            -- 여행ID
    host_user_id       BIGINT                                  NOT NULL,                            -- 호스트ID
    applicant_user_id  BIGINT                                  NOT NULL,                            -- 참가신청자ID
    status             ENUM('PENDING','ACCEPTED','REJECTED')   NOT NULL DEFAULT 'PENDING',          -- 참가신청상태
    created_at         DATETIME                                NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 생성일자

    CONSTRAINT fk_trip_join_request_trip_article_id   FOREIGN KEY (trip_article_id)   REFERENCES trip_article (id),
    CONSTRAINT fk_trip_join_request_host_user_id      FOREIGN KEY (host_user_id)      REFERENCES users        (id),
    CONSTRAINT fk_trip_join_request_applicant_user_id FOREIGN KEY (applicant_user_id) REFERENCES users        (id)
);
-- [여행후기 관련]
-- 여행 리뷰
CREATE TABLE trip_reviews (
    id                 BIGINT       AUTO_INCREMENT PRIMARY KEY,                               -- 여행리뷰ID
    user_id            BIGINT       NOT NULL,                                                 -- 여행참가자ID
    trip_article_id    BIGINT       NOT NULL,                                                 -- 여행일정ID
    rating             INT          NOT NULL,                                                 -- 평점
    title              VARCHAR(50)  NOT NULL,                                                 -- 제목
    content            TEXT,                                                                  -- 내용
    is_public          BOOLEAN      NOT NULL,                                                 -- 공개유무
    created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,                       -- 생성일자

    CONSTRAINT fk_trip_reviews_user_id         FOREIGN KEY (user_id)         REFERENCES users        (id),
    CONSTRAINT fk_trip_reviews_trip_article_id FOREIGN KEY (trip_article_id) REFERENCES trip_article (id)
);
-- 여행 리뷰 이미지
CREATE TABLE trip_reviews_image (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,  -- 여행리뷰 이미지ID
    trip_reviews_id     BIGINT          NOT NULL,                    -- 여행리뷰ID
    image_path          VARCHAR(255)    NULL,                        -- 이미지
    order_number        INT             NULL,                        -- 이미지 표시 순서

    CONSTRAINT fk_trip_reviews_image_trip_reviews_id FOREIGN KEY (trip_reviews_id) REFERENCES trip_reviews (id)
);
-- 사용자 간 리뷰
CREATE TABLE user_by_user_review (
    id                      BIGINT  AUTO_INCREMENT PRIMARY KEY, -- 사용자 간 리뷰 고유ID
    user_id                 BIGINT  NOT NULL,                   -- 주최자를 리뷰하는 유저 고유ID
    chat_participants_id    BIGINT  NOT NULL,                   -- 여행 참가자(마지막 챗참가자) ID
    reviewed_user_id        BIGINT  NOT NULL,                   -- 리뷰를 받는 주최자 고유ID
    rating                  INT     NOT NULL,                   -- 별점

    CONSTRAINT fk_user_by_user_review_user_id              FOREIGN KEY (user_id)              REFERENCES users             (id),
    CONSTRAINT fk_user_by_user_review_reviewed_user_id     FOREIGN KEY (reviewed_user_id)     REFERENCES users             (id),
    CONSTRAINT fk_user_by_user_review_chat_participants_id FOREIGN KEY (chat_participants_id) REFERENCES chat_participants (id)
);
-- [채팅 관련]
-- 채팅방
CREATE TABLE chat_room (
    id                 BIGINT    AUTO_INCREMENT PRIMARY KEY,   -- 채팅방ID
    trip_article_id    BIGINT    NOT NULL,                     -- 여행일정ID
    created_at         DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,   -- 생성일자
    deleted_at         DATETIME,                               -- 삭제일자

    CONSTRAINT  fk_chat_room_trip_article_id FOREIGN KEY (trip_article_id) REFERENCES  trip_article (id)
);
-- 채팅참가자
CREATE TABLE chat_participants (
    id             BIGINT                              AUTO_INCREMENT PRIMARY KEY,          -- 채팅참가자ID
    chat_room_id   BIGINT                              NOT NULL,                            -- 채팅방ID
    user_id        BIGINT                              NOT NULL,                            -- 채팅에 참여한 유저ID
    role           ENUM('HOST','PARTICIPANT')          NOT NULL DEFAULT 'PARTICIPANT',      -- 역할
    status         ENUM('ACTIVE','LEFT','COMPLETED')   NOT NULL DEFAULT 'ACTIVE',           -- 참가상태
    created_at     DATETIME                            NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 생성일자
    deleted_at     DATETIME,                                                                -- 삭제일자

    CONSTRAINT fk_chat_participants_chat_room_id FOREIGN KEY (chat_room_id) REFERENCES chat_room (id),
    CONSTRAINT fk_chat_participants_user_id      FOREIGN KEY (user_id)      REFERENCES users     (id)
);
-- 채팅메세지
CREATE TABLE chat_message (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,          -- 채팅 메세지ID
    user_id        BIGINT        NOT NULL,                            -- 발신자
    chat_room_id   BIGINT        NOT NULL,                            -- 채팅방ID
    message        TEXT              NULL,                            -- 메세지
    image_path     VARCHAR(255)      NULL,                            -- 이미지
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 생성일자
    deleted_at     DATETIME,                                          -- 삭제일자

    CONSTRAINT fk_chat_message_user_id       FOREIGN KEY (user_id)      REFERENCES users     (id),
    CONSTRAINT fk_chat_message_chat_room_id FOREIGN KEY (chat_room_id) REFERENCES chat_room (id)
);
-- [고객센터 관련]
-- Q&A 질문
CREATE TABLE qna_questions (
    id                  BIGINT                                          AUTO_INCREMENT PRIMARY KEY,                                         -- Q&A 질문 고유ID
    user_id             BIGINT                                          NOT NULL,                                                           -- 사용자ID
    trip_article_id     BIGINT,                                                                                                             -- 여행일정ID(여행관련 질문인 경우)
    type                ENUM('COMMON','ACCOUNT','PAYMENT','BUG','ETC')  NOT NULL   DEFAULT 'COMMON',                                        -- 질문유형('COMMON':일반,'ACCOUNT':계정,'PAYMENT':결제,'BUG':버그신고,'ETC':기타)
    title               VARCHAR(50)                                     NOT NULL,                                                           -- 제목
    content             TEXT                                            NOT NULL,                                                           -- 내용
    status              ENUM('PROCESSING','DONE')                       NOT NULL   DEFAULT 'PROCESSING',                                    -- 상태('PROCESSING':답변대기,'DONE':답변완료)
    created_at          DATETIME                                        NOT NULL   DEFAULT CURRENT_TIMESTAMP,                               -- 생성일자
    updated_at          DATETIME                                                   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,   -- 수정일자
    deleted_at          DATETIME,                                                                                                           -- 삭제일자

    CONSTRAINT fk_qna_questions_user_id         FOREIGN KEY (user_id)         REFERENCES users        (id),
    CONSTRAINT fk_qna_questions_trip_article_id FOREIGN KEY (trip_article_id) REFERENCES trip_article (id)
);
-- Q&A 질문 이미지
CREATE TABLE qna_question_images (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,   -- Q&A 질문 이미지 고유ID
    question_id     BIGINT          NOT NULL,                     -- Q&A 질문ID
    image_path      VARCHAR(255),                                 -- 이미지
    order_number    INT,                                          -- 이미지 표시 순서

    CONSTRAINT fk_qna_question_images_question_id FOREIGN KEY (question_id) REFERENCES qna_questions (id)
);
-- Q&A 답변
CREATE TABLE qna_answers (
    id               BIGINT      AUTO_INCREMENT PRIMARY KEY,                                         -- Q&A 답변 고유ID
    user_id          BIGINT      NOT NULL,                                                           -- Q&A 질문자ID
    qna_question_id  BIGINT      NOT NULL,                                                           -- Q&A 질문글ID
    content          TEXT        NOT NULL,                                                           -- 내용
    created_at       DATETIME    NOT NULL    DEFAULT CURRENT_TIMESTAMP,                              -- 생성일자
    updated_at       DATETIME                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 수정일자

    CONSTRAINT fk_qna_answers_user_id         FOREIGN KEY (user_id)         REFERENCES users         (id),
    CONSTRAINT fk_qna_answers_qna_question_id FOREIGN KEY (qna_question_id) REFERENCES qna_questions (id)
);
-- [알림 관련]
-- 시스템알림
CREATE TABLE system_notification (
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,          -- 알림ID
    user_id      BIGINT        NOT NULL ,                           -- 알림 받는 사용자
    type         VARCHAR(50)   NOT NULL,                            -- 알림 종류 (TRIP_START, REVIEW_RECEIVED, PASSWORD_RESET 등)
    title        VARCHAR(100)  NOT NULL,                            -- 알림 제목
    link         VARCHAR(255),                                      -- 관련 페이지 (예: trip_detail?id=123)
    is_read      BOOLEAN       NOT NULL DEFAULT FALSE,              -- 읽음 여부
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 생성일자
    expired_at   DATETIME,                                          -- 유효기간 (선택)

    CONSTRAINT fk_system_notification_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);