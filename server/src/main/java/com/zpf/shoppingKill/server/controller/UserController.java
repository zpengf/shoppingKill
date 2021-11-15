package com.zpf.shoppingKill.server.controller;

import com.zpf.shoppingKill.api.enums.StatusCode;
import com.zpf.shoppingKill.api.reponse.BaseResponse;
import com.zpf.shoppingKill.server.dto.KillDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: UserController
 * @Author: pengfeizhang
 * @Description: 用户控制器
 * @Date: 2021/11/13 下午7:17
 * @Version: 1.0
 */
@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(KillController.class);

    /***
     * 跳到登陆页
     * @return
     */
    @RequestMapping(value = {"to/login","unauth"})
    public String toLogin(){
        return "login";
    }

    @RequestMapping(value = {"/login"},method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam String userName, @RequestParam String password, ModelMap modelMap){

        String errorMsg = "";
        try {
            if(SecurityUtils.getSubject().isAuthenticated()){
                //封装 一个token
                UsernamePasswordToken token = new UsernamePasswordToken(userName,password);
                SecurityUtils.getSubject().login(token);
            }
        } catch (Exception e){
            errorMsg = "用户登陆异常";
            e.printStackTrace();
        }
        if(StringUtils.isEmpty(errorMsg)){
            return "redirect:/index";
        } else {
            modelMap.addAttribute("errorMsg",errorMsg);
            return "login";
        }
    }




    /***
     * 跳到登陆页
     * @return
     */
    @RequestMapping(value = "logOut")
    public String logOut(){
        SecurityUtils.getSubject().logout();
        return "login";
    }



}
