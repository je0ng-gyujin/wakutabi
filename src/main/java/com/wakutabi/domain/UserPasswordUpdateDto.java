package com.wakutabi.domain;
	

	
import lombok.Data;
import lombok.NoArgsConstructor;
	
	@Data
	@NoArgsConstructor
	public class UserPasswordUpdateDto {
		private String username; 
		private String currentPassword;
		private String newPassword;
		private String confirmNewPassword;
}
