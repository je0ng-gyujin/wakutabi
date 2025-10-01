package com.wakutabi.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // 발신자 이메일 (application.properties의 spring.mail.username과 동일해야 함)
    private static final String FROM_ADDRESS = "jsngad@naver.com";
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

            // 발신자 지정 (네이버 SMTP는 반드시 필요)
            helper.setFrom(FROM_ADDRESS, FROM_NAME);

            mailSender.send(message);
        } catch (MessagingException e) {
            // 메일 전송 실패 시 로그 찍고 런타임 예외 던지기
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            // setFrom의 두 번째 파라미터(이름) 관련 예외 대비
            throw new RuntimeException("이메일 전송 중 알 수 없는 오류 발생: " + e.getMessage(), e);
        }
    }
}
