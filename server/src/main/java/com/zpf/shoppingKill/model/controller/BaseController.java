package com.zpf.shoppingKill.model.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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


}
