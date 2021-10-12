package com.zpf.shoppingKill.server.service;

/**
 * @ClassName: IKillService
 * @Author: pengfeizhang
 * @Description: 秒杀service
 * @Date: 2021/10/12 下午7:28
 * @Version: 1.0
 */
public interface IKillService {

    Boolean killItem(Integer killId,Integer userId) throws Exception;

    Boolean killItemV2(Integer killId, Integer userId) throws Exception;

    Boolean killItemV3(Integer killId, Integer userId) throws Exception;

    Boolean killItemV4(Integer killId, Integer userId) throws Exception;

    Boolean killItemV5(Integer killId, Integer userId) throws Exception;
}
