package com.wakutabi.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpDto {
	private String username; 
	private String nickname;                    
	private String password; 
    private LocalDate birth;                               
    private String email;                                              
    private LocalDateTime createdAt; 
    private Role role;
    public enum Role {
        USER,   // 일반 사용자
        ADMIN   // 관리자
    }

}
