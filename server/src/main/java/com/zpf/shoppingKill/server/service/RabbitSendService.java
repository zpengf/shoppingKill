package com.zpf.shoppingKill.server.service;

import com.zpf.shoppingKill.model.entity.ItemKillSuccess;
import com.zpf.shoppingKill.model.mapper.ItemKillMapper;
import com.zpf.shoppingKill.model.mapper.ItemKillSuccessMapper;
import com.zpf.shoppingKill.server.service.impl.IKillServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @ClassName: RabbitSendService
 * @Author: pengfeizhang
 * @Description: rabbit 发送服务
 * @Date: 2021/11/6 下午5:57
 * @Version: 1.0
 */

@Service
public class RabbitSendService {

    private static final Logger log= LoggerFactory.getLogger(IKillServiceImpl.class);


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private Environment env;


    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;


    /**
     * 秒杀成功 异步发送邮件通知
     *
     */
    public void sendSuccessEmailMsg(String orderNo){
        log.info("秒杀成功准备发送消息订单编号{}",orderNo);
        try {
            if (StringUtils.isNotEmpty(orderNo)){
                ItemKillSuccess itemKillSuccess =  itemKillSuccessMapper.queryByCode(orderNo);
                if(null != itemKillSuccess){
                    //将itemKillSuccess 发送到队列

                    //发送消息逻辑
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.routing.key"));


                    //itemKillSuccess 充当消息发送出去
                    rabbitTemplate.convertAndSend(itemKillSuccess, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            //获取消息属性
                            MessageProperties messageProperties = message.getMessageProperties();
                            //保证消息的可靠性 设置持久化
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                            //设置消息头
                            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_KEY_CLASSID_FIELD_NAME,ItemKillSuccess.class);
                            return message;
                        }
                    });
                }
            }



        } catch (Exception e){
            log.error("发生异常");
        }

    }

    /**
     * 抢购成功后 发送到死信队列 一段时间后判断是否失效此订单
     * @param orderNo
     */

    public void sendKillSuccessOrderExpireMsg(String orderNo) {
        try {
            if(StringUtils.isNotEmpty(orderNo)){
                ItemKillSuccess itemKillSuccess =  itemKillSuccessMapper.queryByCode(orderNo);
                if(null != itemKillSuccess){
                    //发送消息逻辑
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

                    //设置基本交换机
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.kill.dead.prod.exchange"));

                    //基本路由
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.kill.dead.prod.routing.key"));

                    //itemKillSuccess 充当消息发送出去
                    rabbitTemplate.convertAndSend(itemKillSuccess, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            //获取消息属性
                            MessageProperties messageProperties = message.getMessageProperties();
                            //保证消息的可靠性 设置持久化
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                            //设置消息头
                            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_KEY_CLASSID_FIELD_NAME,ItemKillSuccess.class);

                            //动态设置ttl 从配置文件去取
                            messageProperties.setExpiration(env.getProperty("mq.kill.item.success.kill.expire"));

                            return message;
                        }
                    });



                }


            }





        } catch (Exception e){

        }


    }
}
