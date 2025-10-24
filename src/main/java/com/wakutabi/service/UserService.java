package com.wakutabi.service;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.UserPasswordUpdateDto;
import com.wakutabi.domain.UserUpdateDto;
import com.wakutabi.mapper.UserMapper;

import jakarta.servlet.http.HttpSession;

import java.util.UUID; 

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	
	@Autowired
	private PasswordEncoder passwordEncoder; // Spring Security 사용 시
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private EmailService emailService;
	
	// (세션 만료 시간 - 예: 5분)
	private static final long CODE_EXPIRATION_MILLIS = 1000 * 60 * 5;
	public int countByUsername(String username) {
		int result = userMapper.countByUsername(username);
		return result;
	}
	// 이메일 중복 검사
	public int countByEmail(String email){
		return userMapper.countByEmail(email);
	}

	// 회원 이메일 인증
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
	
	/**
	 * [아이디 찾기] 1. 인증번호 발송
	 */
	public boolean sendVerificationCodeForFindId(String email, HttpSession session) {
	    // 1. 이메일이 DB에 존재하는지 확인
	    if (!userMapper.existsByEmail(email)) {
	        return false; // 존재하지 않는 이메일
	    }

	    // 2. 이메일 발송
	    String code = emailService.sendVerificationCode(email);

	    // 3. 세션에 인증번호와 만료 시간 저장
	    long expiryTime = System.currentTimeMillis() + CODE_EXPIRATION_MILLIS;
	    session.setAttribute("verificationCode", code);
	    session.setAttribute("codeExpiryTime", expiryTime);
	    session.setAttribute("verifiedEmail", email); // 어떤 이메일에 대한 인증인지 저장

	    return true;
	}
	

	/**
	 * [아이디 찾기] 2. 인증번호 확인 및 아이디 반환
	 */
	public String verifyCodeAndFindUsername(String email, String inputCode, HttpSession session) {
	    String sessionCode = (String) session.getAttribute("verificationCode");
	    Long expiryTime = (Long) session.getAttribute("codeExpiryTime");
	    String verifiedEmail = (String) session.getAttribute("verifiedEmail");

	    // 1. 세션 정보 확인 (시간 초과, 이메일 불일치, 코드 불일치)
	    if (sessionCode == null || expiryTime == null || verifiedEmail == null ||
	        System.currentTimeMillis() > expiryTime) {
	        session.removeAttribute("verificationCode");
	        session.removeAttribute("codeExpiryTime");
	        session.removeAttribute("verifiedEmail");
	        return null; // "인증 시간이 만료되었습니다."
	    }

	    if (!verifiedEmail.equals(email) || !sessionCode.equals(inputCode)) {
	        return null; // "인증번호가 일치하지 않습니다."
	    }

	    // 2. 인증 성공: 아이디 찾기 및 세션 정리
	    String username = userMapper.findUsernameByEmail(email);
	    
	    session.removeAttribute("verificationCode");
	    session.removeAttribute("codeExpiryTime");
	    session.removeAttribute("verifiedEmail");

	    return username;
	}
	
	
	/**
	 * [비밀번호 찾기] 1. 인증번호 발송
	 * (아이디 찾기의 sendVerificationCodeForFindId 메서드와 로직 동일. 재사용 가능)
	 */
	public boolean sendVerificationCodeForResetPw(String email, HttpSession session) {
	    // 1. 이메일 존재 확인
	    if (!userMapper.existsByEmail(email)) {
	        return false;
	    }
	    // 2. 이메일 발송
	    String code = emailService.sendVerificationCode(email);
	    // 3. 세션 저장
	    long expiryTime = System.currentTimeMillis() + CODE_EXPIRATION_MILLIS;
	    session.setAttribute("resetPwCode", code);
	    session.setAttribute("resetPwExpiryTime", expiryTime);
	    session.setAttribute("resetPwEmail", email);
	    return true;
	}

	/**
	 * [비밀번호 찾기] 2. 인증번호 확인 및 비밀번호 재설정
	 */
	public boolean verifyCodeAndResetPassword(String email, String inputCode, String newPassword, HttpSession session) {
	    String sessionCode = (String) session.getAttribute("resetPwCode");
	    Long expiryTime = (Long) session.getAttribute("resetPwExpiryTime");
	    String verifiedEmail = (String) session.getAttribute("resetPwEmail");

	    // 1. 세션 정보 확인
	    if (sessionCode == null || expiryTime == null || verifiedEmail == null ||
	        System.currentTimeMillis() > expiryTime ||
	        !verifiedEmail.equals(email) || !sessionCode.equals(inputCode)) {
	        
	        // 인증 실패 시 세션 정보 즉시 삭제
	        session.removeAttribute("resetPwCode");
	        session.removeAttribute("resetPwExpiryTime");
	        session.removeAttribute("resetPwEmail");
	        return false; // "인증에 실패했습니다."
	    }

	    // 2. 인증 성공: 비밀번호 해시 및 DB 업데이트
	    String hashedPassword = passwordEncoder.encode(newPassword); // ★★★ 중요: 비밀번호 해시
	    userMapper.updatePasswordByEmail(email, hashedPassword);
	    
	    // 3. 세션 정리
	    session.removeAttribute("resetPwCode");
	    session.removeAttribute("resetPwExpiryTime");
	    session.removeAttribute("resetPwEmail");

	    return true;
	}

	public String getUsernameById(Long userId){
		return userMapper.getUsernameById(userId);
	}
}