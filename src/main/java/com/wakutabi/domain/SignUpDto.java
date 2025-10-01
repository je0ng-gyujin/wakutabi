	package com.wakutabi.domain;
	
	import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
	
	@Data
	@NoArgsConstructor
	public class SignUpDto {
		 @NotEmpty(message = "아이디는 필수 항목입니다.")
		    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
		    private String username;

		    @NotEmpty(message = "닉네임은 필수 항목입니다.")
		    private String nickname;

		    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
		    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
		    private String password;

		    @NotNull(message = "생년월일은 필수 항목입니다.")
		    private LocalDate birth;

		    @NotEmpty(message = "이메일은 필수 항목입니다.")
		    @Email(message = "유효한 이메일 형식이 아닙니다.")
		    private String email;                                             
	    private LocalDateTime createdAt; 
	    private Role role;
	    private String verificationToken; // 👈 이메일 인증을 위한 토큰
	    private boolean isVerified;      // 👈 인증 상태 (기본값 false)
	    public enum Role {
	        USER,   // 일반 사용자
	        ADMIN   // 관리자
	    }
	
	}
