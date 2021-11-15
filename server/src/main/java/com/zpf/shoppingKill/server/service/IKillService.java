package com.zpf.shoppingKill.server.service;

/**
 * @ClassName: IKillService
 * @Author: pengfeizhang
 * @Description: 秒杀service
 * @Date: 2021/10/12 下午7:28
 * @Version: 1.0
 */
public interface IKillService {

    /**
     *  最基本基本逻辑处理
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    Boolean killItem(Integer killId,Integer userId) throws Exception;

    /**
     * 从 数据库层面优化
     * 控制库存不能为负数  但是不能控制一个用户抢到多个商品
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    Boolean killItemV2(Integer killId, Integer userId) throws Exception;


    /**
     *  基于redis的缓存分布式锁实现 优化 redis的原子操作 需要配置
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    Boolean killItemV3(Integer killId, Integer userId) throws Exception;


    /**
     *  redisson 优化redis
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    Boolean killItemV4(Integer killId, Integer userId) throws Exception;



    /**
     *  基于zookeeper 分布式锁
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    Boolean killItemV5(Integer killId, Integer userId) throws Exception;
}
