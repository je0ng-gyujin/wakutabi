	package com.wakutabi.domain;
	
	import java.time.LocalDate;
	import java.time.LocalDateTime;
	
	import lombok.Data;
import lombok.NoArgsConstructor;
	
	@Data
	@NoArgsConstructor
	public class SignUpDto {
		private String username; 
		private String nickname;                    
		private String password; 
	    private LocalDate birth;                               
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
