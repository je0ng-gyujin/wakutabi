package com.wakutabi.domain;
	
import java.time.LocalDate;
	
import lombok.Data;
import lombok.NoArgsConstructor;
	
	@Data
	@NoArgsConstructor
	public class UserUpdateDto {
		private Long id;
		private String username; 
		private String nickname;
		private String email;                    
	    private LocalDate birth;
		private Gender gender;
		private Boolean isPublic;
		private String introduce;
		private double rating;
		private String imagePath;

		public enum Gender{
			MALE,
			FEMALE,
			OTHER,
			NONE
		}  
}
