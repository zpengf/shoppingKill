package com.zpf.shoppingKill.model.controller;


import com.zpf.shoppingKill.model.enums.StatusCode;
import com.zpf.shoppingKill.model.reponse.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 基础controller
 *
 */
@Controller
@RequestMapping("/base")
public class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);




    //@RequestMapping(value = "/wel",method = RequestMethod.GET)
    @GetMapping("/wel")
    public String wel(){

        return "222";
    }

    @RequestMapping(value = "/reponse",method = RequestMethod.GET)
    public BaseResponse response(){
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);


        return baseResponse;
    }

}
