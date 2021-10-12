package com.zpf.shoppingKill.server.service.impl;

import com.zpf.shoppingKill.server.controller.ItemController;
import com.zpf.shoppingKill.model.entity.ItemKill;
import com.zpf.shoppingKill.model.mapper.ItemKillMapper;
import com.zpf.shoppingKill.server.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ItemServiceImpl
 * @Author: pengfeizhang
 * @Description: 商品业务实现类
 * @Date: 2021/9/27 下午8:57
 * @Version: 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger Logs = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemKillMapper itemKillMapper;



    @Override
    public List<ItemKill> getItemKill() throws Exception {
        return itemKillMapper.selectAll();
    }

    @Override
    public ItemKill getItemKillDetail(Integer id) throws Exception {
        ItemKill item =  itemKillMapper.selectByItemId(id);
        if(null == item){
            throw new Exception();
        }
        return item;
    }
}
