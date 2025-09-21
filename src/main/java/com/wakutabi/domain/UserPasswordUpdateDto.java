package com.wakutabi.domain;
	

	
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
	
	@Data
	@NoArgsConstructor
	public class UserPasswordUpdateDto {
		private String username;
		@NotBlank(message = "현재 비밀번호를 적어주세요.")
		private String currentPassword;
		@NotBlank(message = "새 비밀번호를 적어주세요.")
		@Size(min = 5, max = 15, message = "새 비밀번호는 3자 이상 15자 이하로 적어주세요.")
		@Pattern(
				regexp = "^(?=.*[!@#$%^(),.?\":{}|<>]).+$",
				message = "비밀번호에는 최소 1개의 특수문자가 포함되어야 합니다."
		)
		private String newPassword;
		@NotBlank(message = "비밀번호를 확인해주세요.")
		private String confirmNewPassword;
}
