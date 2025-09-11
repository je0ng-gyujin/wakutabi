package com.wakutabi.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDto {
	private String username; 
	private String password;
}
