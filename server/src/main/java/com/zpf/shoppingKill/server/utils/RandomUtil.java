package com.zpf.shoppingKill.server.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName: RandomUtil
 * @Author: pengfeizhang
 * @Description: 随机数生成
 * @Date: 2021/9/28 下午8:49
 * @Version: 1.0
 */
public class RandomUtil {

    private static final SimpleDateFormat dateFormatOne = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static String generateOrderCode(){
       return dateFormatOne.format(DateTime.now().toDate()) + generatorNumber(4);
    }

    public static String generatorNumber(final int num){

        StringBuffer sb = new StringBuffer();
        for(int i = 1;i <= num;i++){
            sb.append(random.nextInt(9));
        }

        return sb.toString();
    }
}
