package com.example.searchbusanshopapi.user.service;

import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.pretty.MessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.tags.ThemeTag;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class MailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    /**
     * 랜덤값을 생성해 db에 넣은뒤 그대로 사용자메일에도
     * 보냅니다.
     * @param userId
     * @throws MessagingException
     */
    public void sendAuthenticationMail(Long userId) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        String mailToken = UUID.randomUUID().toString();

        User user = userRepository.findById(userId).get();
        user.setMailToken(mailToken);

        try {
            MimeMessageHelper messageHelper =
                    new MimeMessageHelper(message, true, "UTF-8");
            //현재 테스트중이여서 모두 작성자 본인의 메일을 사용하였습니다.
            messageHelper.setTo("kma952727@gmail.com");
            messageHelper.setFrom("kma952727@gmail.com");
            messageHelper.setSubject("부산가게에서 발송되었습니다.");
            messageHelper.setText(renderAuthenticationHTML(mailToken, userId), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 링크 쿼리파라미터값에있는 토큰값과 db에 저장된 토큰값을 비교합니다.
     * 일치할시 user메일체크여부를 true로 저장합니다.(가입시 default = false)
     * @param userId
     * @param mailToken
     * @return
     */
    public boolean checkAuthenticationMail(Long userId, String mailToken) {
        Boolean checkResult = userRepository.existsByMailTokenAndId(mailToken, userId);
        if(checkResult) {
            User user = userRepository.findById(userId).get();
            user.setMailCheck(true);
            return true;
        }
        return false;
    }

    /**
     * 보낼 html파일을 작성합니다.
     * @param mailToken
     * @param userId
     * @return
     */
    private String renderAuthenticationHTML(String mailToken, Long userId){
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<body>");
        html.append("<a href=\"localhost:8088/users/");
        html.append(userId);
        html.append("/mail/authentication?mailToken=");
        html.append(mailToken);
        html.append("\">메일 인증하기</a> 링크를 클릭하시면 메일인증이 완료됩니다.");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}
