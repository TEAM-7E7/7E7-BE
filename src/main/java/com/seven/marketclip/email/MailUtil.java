package com.seven.marketclip.email;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Component
public class MailUtil {

    private final MailProperties mailProperties;
    private final TemplateEngine htmlTemplateEngine;
    private final JavaMailSender javaMailSender;

    public MailUtil(MailProperties mailProperties, TemplateEngine htmlTemplateEngine, JavaMailSender javaMailSender) {
        this.mailProperties = mailProperties;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendTemplateMail(String toMail, String subject, String fromName, Map<String, Object> variables)
            throws Exception {
        Context context = new Context();
        context.setVariables(variables);

        InternetAddress from = new InternetAddress(mailProperties.getUsername(), fromName);
        InternetAddress to = new InternetAddress(toMail);

        String htmlTemplate = htmlTemplateEngine.process("mail", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(htmlTemplate, true);

        javaMailSender.send(mimeMessage);
    }
}