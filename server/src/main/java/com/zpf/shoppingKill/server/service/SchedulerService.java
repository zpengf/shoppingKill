package com.zpf.shoppingKill.server.service;

import com.zpf.shoppingKill.model.entity.ItemKillSuccess;
import com.zpf.shoppingKill.model.mapper.ItemKillSuccessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @ClassName: SchedulerService
 * @Author: pengfeizhang
 * @Description: 死信补充 定时处理过期订单
 * @Date: 2021/11/13 下午2:55
 * @Version: 1.0
 */
@Service
public class SchedulerService {


    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;


    @Autowired
    private Environment env;

    @Scheduled(cron = "0/10 * * * * ?")
    public void schedulerExpireOrder(){

        try {
            ArrayList<ItemKillSuccess> successArrayList = itemKillSuccessMapper.selectAllPayOrder();
            if(null != successArrayList && !successArrayList.isEmpty()){
                successArrayList.stream().forEach(new Consumer<ItemKillSuccess>() {
                    @Override
                    public void accept(ItemKillSuccess itemKillSuccess) {
                        if(itemKillSuccess != null
                                && (itemKillSuccess.getTimeStampDiff() >= env.getProperty("scheduler.expire.orders.time",Integer.class))){
                            itemKillSuccessMapper.expireOrder(itemKillSuccess.getCode());
                        }
                    }
                });
            }
            // 异步处理  或者分开多线程处理
            for(ItemKillSuccess ks:successArrayList){

            }

        } catch (Exception e) {

        }


    }



}
