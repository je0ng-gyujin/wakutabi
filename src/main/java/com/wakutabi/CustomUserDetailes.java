package com.wakutabi;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.wakutabi.domain.LoginDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetailes implements UserDetails{

	private final LoginDto loginDto;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return List.of(new SimpleGrantedAuthority("USER"));
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return loginDto.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return loginDto.getUsername();
	}
	
	

}
