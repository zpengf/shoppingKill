package com.zpf.shoppingKill.server.service;

import com.zpf.shoppingKill.model.entity.ItemKillSuccess;
import com.zpf.shoppingKill.model.mapper.ItemKillSuccessMapper;
import com.zpf.shoppingKill.server.dto.MailDto;
import com.zpf.shoppingKill.server.service.impl.IKillServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * @ClassName: RabbitReceiveService
 * @Author: pengfeizhang
 * @Description: rabbitmq 接收服务
 * @Date: 2021/11/6 下午5:57
 * @Version: 1.0
 */
public class RabbitReceiveService {
    private static final Logger log= LoggerFactory.getLogger(IKillServiceImpl.class);


    @Autowired
    private MailService mailService;

    @Autowired
    private Environment env;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;


    /**
     * 秒杀成功 异步邮件通知
     * @param info
     */
    @RabbitListener(queues = "${mq.kill.item.success.email.queue}",containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(ItemKillSuccess info){
        try {
            log.info("秒杀异步邮件通知",info);
            //开始发送邮件
            MailDto mailDto = new MailDto();
            mailDto.setSubject(env.getProperty("mail.kill.item.success.subject"));
            String content = String.format(env.getProperty("mail.kill.item.success.content"),info.getItemName(),info.getCode());
            mailDto.setContent(content);
            mailDto.setTos(new String[]{info.getEmail()});
            mailService.sendHtmlMail(mailDto);
        } catch (Exception e){

            log.error(e.getMessage());
        }

    }

    /**
     * 消费   处理过期订单
     * @param info
     */
    @RabbitListener(queues = "${mq.kill.item.success.kill.dead.real.queue}",containerFactory = "singleListenerContainer")
    public void consumeExpireOrder(ItemKillSuccess info){
        try {
            log.info("失效订单",info);
            //判断订单是否处于 未支付的状态
            if(null != info){
                //查询此时的状态
                ItemKillSuccess itemKillSuccess = itemKillSuccessMapper.selectByPrimaryKey(info.getCode());
                if(null != itemKillSuccess && 0 == itemKillSuccess.getStatus()){
                    //失效当前订单
                    itemKillSuccessMapper.expireOrder(info.getCode());
                }

            }
        } catch (Exception e){

            log.error("监听处理过期订单发生错误",e.getMessage());
        }

    }





}
