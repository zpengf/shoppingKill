package com.zpf.shoppingKill.server.service;

import com.zpf.shoppingKill.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * @ClassName: MailService
 * @Author: pengfeizhang
 * @Description: 邮件发送逻辑
 * @Date: 2021/11/13 下午1:20
 * @Version: 1.0
 */

@Service
public class MailService {

    private static final Logger logs = LoggerFactory.getLogger(MailService.class);


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;



    /**
     * 采用异步形式  发送邮件
     */
    @Async
    public void sendSimpleMail(final MailDto mailDto){
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setFrom(env.getProperty("mail.send.from"));
            simpleMailMessage.setTo(mailDto.getTos());
            simpleMailMessage.setSubject(mailDto.getSubject());
            simpleMailMessage.setText(mailDto.getContent());
            mailSender.send(simpleMailMessage);
            logs.info("发送邮件成功");
        }catch (Exception e){
            logs.error("发送邮件失败");
            e.printStackTrace();
        }
    }
    /**
     * 采用异步形式  发送邮件
     */
    @Async
    public void sendHtmlMail(final MailDto mailDto){
        try {
            MimeMessage mailMessage =  mailSender.createMimeMessage();
            MimeMessageHelper messageHelper =  new MimeMessageHelper(mailMessage,true,"utf-8");
            messageHelper.setFrom(env.getProperty("mail.send.from"));
            messageHelper.setTo(mailDto.getTos());
            messageHelper.setSubject(mailDto.getSubject());
            messageHelper.setText(mailDto.getContent(),true);
            mailSender.send(mailMessage);
            logs.info("发送邮件成功");
        }catch (Exception e){
            logs.error("发送邮件失败");
            e.printStackTrace();
        }
    }




}
