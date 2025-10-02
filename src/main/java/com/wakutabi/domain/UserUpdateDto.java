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
	public Gender gender;
	private Boolean isPublic;
	private String introduce;
	private double rating;

	public enum Gender{
		MALE,
		FEMALE,
		OTHER,
		NONE
	}
}
