	package com.wakutabi.domain;
	
	import java.time.LocalDate;
	import java.time.LocalDateTime;

	import jakarta.validation.constraints.Email;
	import jakarta.validation.constraints.NotBlank;
	import jakarta.validation.constraints.Pattern;
	import jakarta.validation.constraints.Size;
	import lombok.Data;
import lombok.NoArgsConstructor;
	
	@Data
	@NoArgsConstructor
	public class SignUpDto {
		private String username;
		private String nickname;
		@NotBlank(message = "새 비밀번호를 적어주세요.")
		@Size(min = 9, max = 15, message = "새 비밀번호는 9자 이상 15자 이하로 적어주세요.")
		@Pattern(
				regexp = "^(?=.*[!@#$%^(),.?\":{}|<>]).+$",
				message = "비밀번호에는 최소 1개의 특수문자가 포함되어야 합니다."
		)
		private String password; 
	    private LocalDate birth;
		@NotBlank
		@Email(message = "올바른 이메일 형식을 입력해주세요.")
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
