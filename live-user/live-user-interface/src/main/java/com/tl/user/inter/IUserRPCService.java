package com.tl.user.inter;

import com.tl.user.dto.MsgCheckDTO;
import com.tl.user.dto.UserDTO;
import com.tl.user.dto.UserLoginDTO;

/**
 * 用户数据暴露服务
 */
public interface IUserRPCService {

    UserDTO getUserById(Long userId);

    /**
     * 发送短信验证码
     * @param mobile 手机号
     * @return 验证码
     */
    boolean sendLoginCode(String mobile);

    /**
     * 检查验证码是否正确
     * @param mobile 手机号
     * @param code 验证码
     * @return MsgCheckDTO.checkStatus true表示通过，false拒绝
     */
    MsgCheckDTO checkLoginCode(String mobile, Integer code);

    /**
     * 创建用户并返回token
     * @param userId
     * @return 用户Token
     */
    String createAndSaveLoginToken(Long userId);

    /**
     * 验证token合法性
     * @param token
     * @return 返回UserId。
     */
    String checkToken(String token);
}
