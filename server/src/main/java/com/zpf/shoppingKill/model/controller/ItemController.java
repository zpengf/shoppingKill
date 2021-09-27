package com.zpf.shoppingKill.model.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.zpf.shoppingKill.model.entity.ItemKill;
import com.zpf.shoppingKill.model.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @ClassName: ItemController
 * @Author: pengfeizhang
 * @Description: 商品控制器
 * @Date: 2021/9/27 下午8:44
 * @Version: 1.0
 */

@Controller
public class ItemController {

    private static final Logger Logs = LoggerFactory.getLogger(ItemController.class);

    private static final String prefix = "item";


    @Autowired
    private ItemService itemService;


    @RequestMapping(value = {"/","/index",prefix+"/list",prefix+"/index.html"},method = RequestMethod.GET)
    public String getItemList(ModelMap modelMap){
        try {
            List<ItemKill> itemKillList = itemService.getItemKill();
            modelMap.put("list",itemKillList);
        } catch (Exception e){
            Logs.error("error",e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "list";

    }

    @RequestMapping(value = {prefix+"/detail/{id}"},method = RequestMethod.GET)
    public String getDetail(@PathVariable Integer id, ModelMap modelMap){
        try {
           ItemKill itemKill= itemService.getItemKillDetail(id);

           modelMap.put("detail",itemKill);
        } catch (Exception e){
            Logs.error("error",e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "info";
    }


}
