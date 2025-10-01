package com.wakutabi.service;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.UserPasswordUpdateDto;
import com.wakutabi.domain.UserUpdateDto;
import com.wakutabi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // ⬅️ Value import 추가
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File; // ⬅️ File import 추가
import java.io.IOException; // ⬅️ IOException import 추가
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    // 1. application.properties에서 파일 저장 경로를 주입받습니다.
    @Value("${file.upload.path}")
    private String uploadPath;

    // ... (register, verifyEmail 등 다른 메서드는 기존과 동일)

    /**
     * 2. ⭐ 실제 파일 저장 로직을 구현한 메서드 ⭐
     * @param profileImageFile 컨트롤러에서 받은 이미지 파일
     * @return DB에 저장할 웹 경로 (예: /upload/uuid_filename.jpg)
     * @throws IOException 파일 저장 중 발생할 수 있는 예외
     */
    @Transactional
    public String storeProfileImage(MultipartFile profileImageFile) throws IOException {
        // 파일이 비어있으면 null을 반환
        if (profileImageFile == null || profileImageFile.isEmpty()) {
            return null;
        }

        // 업로드 디렉토리가 존재하는지 확인하고 없으면 생성
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 원본 파일명에서 확장자 추출
        String originalFilename = profileImageFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // UUID를 사용하여 고유한 파일명 생성
        String savedFilename = UUID.randomUUID().toString() + extension;

        // 실제 저장될 전체 경로 생성
        File destinationFile = new File(uploadPath + savedFilename);

        // 파일 저장
        profileImageFile.transferTo(destinationFile);

        // DB에 저장될 웹 경로 반환 (WebConfig에 설정한 /upload/ 경로)
        return "/upload/" + savedFilename;
    }
    
    // ... (getUserInfo, userInfoUpdate 등 다른 메서드는 기존과 동일)
    
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