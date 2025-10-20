package com.wakutabi.service;

import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // 발신자 이메일 (application.properties의 spring.mail.username과 동일해야 함)
    // (네이버 SMTP 사용 시 이 주소와 properties의 username이 일치해야 합니다)
    private static final String FROM_ADDRESS = "jsngad@naver.com";
    
    // HTML 메일에서 사용할 발신자 이름
    private static final String FROM_NAME = "Wakutabi";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * HTML 형식 메일 전송
     * @param to 수신자 이메일
     * @param subject 제목
     * @param htmlContent HTML 본문
     */
    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            // true → 멀티파트 메시지(첨부 가능), UTF-8 설정
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML 본문

            // 발신자 지정 (이름 포함)
            helper.setFrom(FROM_ADDRESS, FROM_NAME);

            mailSender.send(message);
        } catch (MessagingException e) {
            // 메일 전송 실패 시 로그 찍고 런타임 예외 던지기
            throw new RuntimeException("HTML 이메일 전송 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            // setFrom의 두 번째 파라미터(이름) 관련 예외 대비
            throw new RuntimeException("이메일 전송 중 알 수 없는 오류 발생: " + e.getMessage(), e);
        }
    }
    
    // 6자리 랜덤 인증번호 생성
    public String createVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    /**
     * [수정됨] 단순 텍스트 형식 인증번호 발송
     * @param toEmail 수신자 이메일
     * @return 생성된 인증 코드
     */
    public String sendVerificationCode(String toEmail) {
        String code = createVerificationCode();
        String subject = "[WakuTabi] 이메일 인증 코드입니다.";
        String text = "인증 코드는 " + code + " 입니다.";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            // ★★★ 오류 수정: 발신자(From) 주소를 설정합니다. ★★★
            message.setFrom(FROM_ADDRESS);
            
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            
            return code; // 생성된 코드를 반환
        } catch (Exception e) {
            // e.printStackTrace()는 throw new RuntimeException()이 자동으로 처리해줍니다.
            // 실패 시 구체적인 원인을 알기 위해 원본 예외(e)를 함께 넘깁니다.
            throw new RuntimeException("인증 코드 이메일 발송에 실패했습니다.", e);
        }
    }
}