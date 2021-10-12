package com.zpf.shoppingKill.server.service.impl;

import com.zpf.shoppingKill.model.entity.ItemKill;
import com.zpf.shoppingKill.model.entity.ItemKillSuccess;
import com.zpf.shoppingKill.model.mapper.ItemKillMapper;
import com.zpf.shoppingKill.model.mapper.ItemKillSuccessMapper;
import com.zpf.shoppingKill.server.enums.SysConstant;
import com.zpf.shoppingKill.server.service.IKillService;
import com.zpf.shoppingKill.server.utils.RandomUtil;
import com.zpf.shoppingKill.server.utils.SnowFlake;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * @ClassName: IKillServiceImpl
 * @Author: pengfeizhang
 * @Description: 秒杀实现
 * @Date: 2021/10/12 下午7:31
 * @Version: 1.0
 */
@Service
public class IKillServiceImpl implements IKillService {


    private static final Logger log= LoggerFactory.getLogger(IKillServiceImpl.class);

    private SnowFlake snowFlake=new SnowFlake(2,3);

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    private ItemKillMapper itemKillMapper;

//    @Autowired
//    private RabbitSenderService rabbitSenderService;



    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
            //TODO:查询待秒杀商品详情
            ItemKill itemKill=itemKillMapper.selectByItemId(killId);

            //TODO:判断是否可以被秒杀canKill=1?
            if (itemKill!=null && 1==itemKill.getCanKill() ){
                //TODO:扣减库存-减一
                int res=itemKillMapper.updateKillItem(killId);

                //TODO:扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res>0){
                    commonRecordKillSuccessInfo(itemKill,userId);

                    result=true;
                }
            }
        }else{
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV5(Integer killId, Integer userId) throws Exception {
        return null;
    }


    /**
     * 通用的方法-记录用户秒杀成功后生成的订单-并进行异步邮件消息的通知
     * @param kill
     * @param userId
     * @throws Exception
     */
    private void commonRecordKillSuccessInfo(ItemKill kill, Integer userId) throws Exception{
        //TODO:记录抢购成功后生成的秒杀订单记录

        ItemKillSuccess entity=new ItemKillSuccess();
        String orderNo=String.valueOf(snowFlake.nextId());

        //entity.setCode(RandomUtil.generateOrderCode());   //传统时间戳+N位随机数
        entity.setCode(orderNo); //雪花算法
        entity.setItemId(kill.getItemId());
        entity.setKillId(kill.getId());
        entity.setUserId(userId.toString());
        entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        entity.setCreateTime(DateTime.now().toDate());
        int res=itemKillSuccessMapper.insertSelective (entity);



//        //TODO:学以致用，举一反三 -> 仿照单例模式的双重检验锁写法
//        if (itemKillSuccessMapper.countByKillUserId(kill.getId(),userId) <= 0){
//            int res=itemKillSuccessMapper.insertSelective (entity);
//
//            if (res>0){
//                //TODO:进行异步邮件消息的通知=rabbitmq+mail
//                rabbitSenderService.sendKillSuccessEmailMsg(orderNo);
//
//                //TODO:入死信队列，用于 “失效” 超过指定的TTL时间时仍然未支付的订单
//                rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);
//            }
//        }
    }

}
