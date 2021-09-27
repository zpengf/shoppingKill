package com.zpf.shoppingKill.model.service;

import com.zpf.shoppingKill.model.entity.ItemKill;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ItemService
 * @Author: pengfeizhang
 * @Description: 商品业务
 * @Date: 2021/9/27 下午8:54
 * @Version: 1.0
 */
public interface ItemService {

    List<ItemKill> getItemKill() throws Exception;

    ItemKill getItemKillDetail(Integer id) throws Exception;
}
