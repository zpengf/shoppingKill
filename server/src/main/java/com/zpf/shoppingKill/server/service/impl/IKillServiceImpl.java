package com.zpf.shoppingKill.server.service.impl;

import com.zpf.shoppingKill.model.entity.ItemKill;
import com.zpf.shoppingKill.model.entity.ItemKillSuccess;
import com.zpf.shoppingKill.model.mapper.ItemKillMapper;
import com.zpf.shoppingKill.model.mapper.ItemKillSuccessMapper;
import com.zpf.shoppingKill.server.enums.SysConstant;
import com.zpf.shoppingKill.server.service.IKillService;
import com.zpf.shoppingKill.server.service.RabbitSendService;
import com.zpf.shoppingKill.server.utils.RandomUtil;
import com.zpf.shoppingKill.server.utils.SnowFlake;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RabbitSendService rabbitSendService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CuratorFramework curatorFramework;



    //库存超卖的问题 典型就是 没有控制好多个线程对共享的数据的控制

    //原因 真正用于判重插入的语句 晚于前边的判断


    //产生的多个线程对 "同一段操作共享数据的代码" 进行并发操作 从而出现并发安全的问题


    //解决方案 逃不了分布式锁  分布式锁解决

    //协助方案 对于瞬时流量 并发请求 进行限流 目前是接口的限流 有条件还能网关层面的限流
    //辅助方案 集群部署 主备部署

    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        //TODO:判断当前用户是否已经抢购过当前商品
        // 如果高并发进来  对于新用户来说 秒杀成功表还没有记录 所有线程基本都会通过
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
        Boolean result=false;

        //这句没啥优化 就是个count查询
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){

            // 查询秒杀商品详情 需要判断当前可被更减的数量 是否仍还大于0
            ItemKill itemKill=itemKillMapper.selectByItemIdV2(killId);

            if (itemKill!=null && 1==itemKill.getCanKill()
                    && itemKill.getTotal() > 0){
                //TODO:扣减库存-减一 更新前判断total是否大于0
                int res=itemKillMapper.updateKillItemV2(killId);
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

    //SETNX EXPIRE 联合使用
    //setnx 表示 缓存里key不存在 EXPIRE就设置成功
    //缓存里key存在 EXPIRE就设置不成功
    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        Boolean result=false;
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){

            //借助redis 的原子操作实现分布式锁 对共享资源控制
            ValueOperations valueOperations = stringRedisTemplate.opsForValue();
            final String key = new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
            final String value = RandomUtil.generateOrderCode();

            Boolean cacheRedisFlag = valueOperations.setIfAbsent(key,value);

            if(cacheRedisFlag){
                //30秒后自动释放 key 避免死锁
                stringRedisTemplate.expire(key,30, TimeUnit.SECONDS);
               try {
                   ItemKill itemKill=itemKillMapper.selectByItemIdV2(killId);
                   if (itemKill!=null && 1==itemKill.getCanKill()
                           && itemKill.getTotal() > 0){
                       int res=itemKillMapper.updateKillItemV2(killId);
                       if (res>0){
                           commonRecordKillSuccessInfo(itemKill,userId);
                           result=true;
                       }
                   }
               } catch (Exception e){

                   throw new Exception("失败");

               } finally {

                   if(value.equals(valueOperations.get(key).toString())){
                       stringRedisTemplate.delete(key);
                   }

               }

            }

        }else{
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        Boolean result=false;
        final String lockkey = new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
        RLock lock = redissonClient.getLock(lockkey);

        try {

            //lock.lock(10,TimeUnit.SECONDS);
            Boolean cacheRedisFlag = lock.tryLock(30,10,TimeUnit.SECONDS);
            if(cacheRedisFlag){
                if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
                    ItemKill itemKill=itemKillMapper.selectByItemIdV2(killId);
                    if (itemKill!=null && 1==itemKill.getCanKill()
                            && itemKill.getTotal() > 0){
                        int res=itemKillMapper.updateKillItemV2(killId);
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result=true;
                        }
                    }
                }else{
                    throw new Exception("您已经抢购过该商品了!");
                }
            }

        } finally {
            lock.unlock();
            //强制释放
            //lock.forceUnlock();
        }


        return result;
    }

    private static final String pathPrefix = "/kill/zkLock";

    @Override
    public Boolean killItemV5(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        InterProcessMutex mutex = new InterProcessMutex(curatorFramework,pathPrefix+killId+userId);

        try {
            if(mutex.acquire(10L,TimeUnit.SECONDS)){
                if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
                    ItemKill itemKill=itemKillMapper.selectByItemId(killId);

                    if (itemKill!=null && 1==itemKill.getCanKill() ){
                        int res=itemKillMapper.updateKillItem(killId);

                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result=true;
                        }
                    }
                }else{
                    throw new Exception("您已经抢购过该商品了!");
                }
            }
        } catch (Exception e){
            throw new Exception("您已经抢购过该商品了!");
        } finally {
            if(mutex !=null){
                mutex.release();
            }
        }




        return result;
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

        //TODO:学以致用，举一反三 -> 仿照单例模式的双重检验锁写法

        //代价就是高并发下 查询逻辑太多
        if (itemKillSuccessMapper.countByKillUserId(kill.getId(),userId) <= 0){
            int res=itemKillSuccessMapper.insertSelective (entity);

            if (res>0){
                //TODO:进行异步邮件消息的通知=rabbitmq+mail
                rabbitSendService.sendSuccessEmailMsg(orderNo);

                //TODO:入死信队列，用于 “失效” 超过指定的TTL时间时仍然未支付的订单
                rabbitSendService.sendKillSuccessOrderExpireMsg(orderNo);
            }
        }
    }

}
