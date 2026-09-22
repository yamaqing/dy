package com.tl.live.controller;

import com.tl.live.entity.MobileLoginParam;
import com.tl.live.entity.WebResDTO;
import com.tl.user.dto.MsgCheckDTO;
import com.tl.user.dto.UserDTO;
import com.tl.user.dto.UserLoginDTO;
import com.tl.user.inter.IUserMobileRPCService;
import com.tl.user.inter.IUserRPCService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Author： roy
 * Description：
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    //引用Dubbo服务
    @DubboReference(check = false)
    private IUserRPCService userRPCService;

    @DubboReference(check = false)
    private IUserMobileRPCService userMobileRPCService;

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    @PostMapping("/queryUser")
    public WebResDTO getUserByID(Long userId ){
        UserDTO userDTO = userRPCService.getUserById(userId);
        if(null != userDTO){
            return new WebResDTO(WebResDTO.SUCCESS_CODE,userDTO);
        }else{
            return new WebResDTO(WebResDTO.ERROR_CODE,"用户不存在");
        }
    }

    /**
     * 下发短信验证码
     * @param mobile
     * @return
     */
    @PostMapping("/sendSMS")
    public WebResDTO sendSMS(String mobile){
        if(!StringUtils.hasText(mobile)){
            return new WebResDTO(WebResDTO.ERROR_CODE,"请求参数异常");
        }

        if(userRPCService.sendLoginCode(mobile)){
            return new WebResDTO(WebResDTO.SUCCESS_CODE,"SUCCESSED");
        }else{
            return new WebResDTO(WebResDTO.ERROR_CODE,"发送短信失败,请重试");
        }
    }

    /**
     * 手机验证码登录
     * @param param 包含手机号码和验证码
     * @return
     */
    @PostMapping("/mobileLogin")
    public WebResDTO mobileLogin(@RequestBody MobileLoginParam param, HttpServletResponse response){
        String mobile = param.getMobile();
        int code = param.getCode();
        //参数检测
        if(!StringUtils.hasText(mobile)){
            return new WebResDTO(WebResDTO.ERROR_CODE,"请求参数异常");
        }
        if( code < 1000 || code >9999){
            return new WebResDTO(WebResDTO.ERROR_CODE,"验证码格式错误");
        }
        //校验验证码
        MsgCheckDTO msgCheckDTO = userRPCService.checkLoginCode(mobile, code);
        if (!msgCheckDTO.isCheckStatus()) {
            return new WebResDTO(WebResDTO.ERROR_CODE,"请输入正确验证码");
        }
        //验证码通过后，使用手机号完成登录，如果没有登录过，就直接注册
        UserLoginDTO userLoginDTO = userMobileRPCService.login(mobile);

        //登录完成，生成Cookie，返回给前端。
        String token = userRPCService.createAndSaveLoginToken(userLoginDTO.getUserId());
        Cookie cookie = new Cookie("tltk", token);
//        cookie.setDomain("live.tl.com");
//        cookie.setPath("/");
        //cookie有效期，一般他的默认单位是秒
        cookie.setMaxAge(30 * 24 * 3600);
        //加上它，不然web浏览器不会将cookie自动记录下
        response.addCookie(cookie);
        return new WebResDTO(WebResDTO.SUCCESS_CODE,userLoginDTO);
    }
}
