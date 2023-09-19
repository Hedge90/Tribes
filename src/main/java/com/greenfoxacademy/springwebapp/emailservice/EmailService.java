package com.greenfoxacademy.springwebapp.emailservice;

import com.greenfoxacademy.springwebapp.dtos.UserDTO;
import com.greenfoxacademy.springwebapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {
    private JavaMailSender mailSender;

    private UserService userService;

    @Autowired
    public EmailService(JavaMailSender mailSender, UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }

    public void sendVerificationRequest(UserDTO userDTO) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.setFrom("admin@tribes.com");
            message.setRecipients(MimeMessage.RecipientType.TO, userDTO.getEmail());
            message.setSubject("Email verification required");

            String token = getToken();
            userService.saveVerificationTokenForUser(userDTO, token);

            String button =       "<form action=\"http://localhost:8080/verify-email\" method=\"get\" target=\"_blank\"> <input type=\"hidden\" name=\"token\" value=\"" + token + "\" />  <button type=\"submit\">Verify email</button> </form>";

            String htmlContent = "<h1>Dear " + userDTO.getUsername() + ",</h1>"
                    + "<p>We have received a registration attempt using your email address. If the attempt was made by you, please click the link below to verify your email address and begin using our app.</p>"
                    + button;
            message.setContent(htmlContent, "text/html; charset=utf-8");
        } catch (MessagingException exception) {
            throw exception;
        }

        mailSender.send(message);
    }

    private String getToken() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 30;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
