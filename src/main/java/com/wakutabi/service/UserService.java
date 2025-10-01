package com.wakutabi.service;

<<<<<<< HEAD
=======
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

>>>>>>> spring/travel_article_crud_mgk
import com.wakutabi.domain.SignUpDto;
import com.wakutabi.mapper.UserMapper;
import java.util.UUID; 

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
=======
>>>>>>> spring/travel_article_crud_mgk

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	
	public int countByUsername(String username) {
		int result = userMapper.countByUsername(username);
		return result;
	}

	 @Transactional
	    public void register(SignUpDto user) {
	        String pw = passwordEncoder.encode(user.getPassword());
	        user.setPassword(pw);
	        
	        String verificationToken = UUID.randomUUID().toString();
	        user.setVerificationToken(verificationToken);
	        
	        user.setVerified(false);
	        
	        userMapper.insertUser(user);
	        
	        String subject = "Wakutabi 회원가입 이메일 인증";
	        String verificationLink = "http://localhost:8088/user/verify-email?username=" + user.getUsername() + "&token=" + verificationToken;
	        
	        // HTML 형식으로 콘텐츠 생성
	        String htmlContent = "<h2>Wakutabi 회원가입을 환영합니다!</h2>"
	                           + "<p>회원가입을 완료하려면 아래 링크를 클릭해주세요:</p>"
	                           + "<a href=\"" + verificationLink + "\">이메일 인증하기</a>";
	        
	        emailService.sendHtmlMessage(user.getEmail(), subject, htmlContent); // sendSimpleMessage 대신 호출
	    }

	@Transactional
	public boolean verifyEmail(String username, String token){
	    // Retry logic to handle the race condition
	    String storedToken = null;
	    int maxAttempts = 5;
	    long delayMillis = 200; // 200ms delay

	    for (int i = 0; i < maxAttempts; i++) {
	        storedToken = userMapper.findVerificationTokenByUsername(username);
	        if (storedToken != null) {
	            break; // Token found, break the loop
	        }
	        try {
	            Thread.sleep(delayMillis);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            return false;
	        }
	    }

	    if (storedToken != null && storedToken.equals(token)){
	        userMapper.updateUserVerificationStatus(username, true);
	        return true;
	    }
	    return false;
	}
<<<<<<< HEAD
	
	public Long getUserId(String username){
		Long userId = userMapper.getUserId(username);
		return userId;
	}

	public UserUpdateDto getUserInfo(String username){
		UserUpdateDto user = userMapper.getUserInfo(username);
		return user;
	}
	
	public void userInfoUpdate(UserUpdateDto user){
		userMapper.userInfoUpdate(user);
	}
	
	public boolean checkCurrentPassword(String username, String currentPassword){
		String encodedPassword = userMapper.findPasswordByUsername(username);
		if(encodedPassword == null){
			return false;
		}
		return passwordEncoder.matches(currentPassword, encodedPassword);
	}
	
	public void userPasswordUpdate(UserPasswordUpdateDto newPassword){
		String pw = passwordEncoder.encode(newPassword.getNewPassword());
		newPassword.setNewPassword(pw);

		userMapper.userPasswordUpdate(newPassword);
	}
	
	public void userWithdrawal(String username){
		userMapper.userWithdrawal(username);
	}
}
=======
}
>>>>>>> spring/travel_article_crud_mgk
