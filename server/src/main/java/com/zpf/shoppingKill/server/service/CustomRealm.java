package com.zpf.shoppingKill.server.service;

import com.zpf.shoppingKill.model.entity.User;
import com.zpf.shoppingKill.model.mapper.UserMapper;
import com.zpf.shoppingKill.server.service.impl.IKillServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.omg.CORBA.UnknownUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @ClassName: CustomRealm
 * @Author: pengfeizhang
 * @Description: 用户自定义 用于shiro认证授权
 * @Date: 2021/11/13 下午7:27
 * @Version: 1.0
 */
public class CustomRealm extends AuthorizingRealm {

    private static final Logger log= LoggerFactory.getLogger(CustomRealm.class);



    @Autowired
    private UserMapper userMapper;
    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {



        return null;
    }

    /**
     * 认证 登陆
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;
        String userName = token.getUsername();
        String password = String.valueOf(token.getPassword());

        User user = userMapper.selectByUserName(userName);

        if(null == user){
            throw new UnknownAccountException("用户名不存在");
        }
        if(!Objects.equals(1,user.getIsActive())){
            throw new DisabledAccountException("当前账户禁用");
        }

        if(!Objects.equals(password,user.getPassword())){
            throw new IncorrectCredentialsException("用户名密码不匹配");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUserName(),password,getName());

        setSession("uid",user.getId());

        return info;
    }


    /**
     * 将key对应的value 放入shiro的session中 最终交给Httpsession
     * @param key
     * @param value
     */
    private void setSession(String key,Object value){
        Session session = SecurityUtils.getSubject().getSession();
        if(null != session){
            session.setAttribute(key,value);
            session.setTimeout(30000L);
        }

    }




}
