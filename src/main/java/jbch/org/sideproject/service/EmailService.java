package jbch.org.sideproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[lifeDeveloperLSH] 회원가입 인증번호입니다.");

            // 간단한 HTML 형식의 이메일 본문
            String htmlContent = "<h1>회원가입 인증번호</h1>"
                             + "<p>인증번호: <strong>" + code + "</strong></p>"
                             + "<p>이 인증번호를 회원가입 화면에 입력해주세요.</p>";

            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // 실무에서는 로깅 처리가 필요합니다.
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }
    }
}
