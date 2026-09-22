package com.tl.user.inter;

import com.tl.user.dto.UserLoginDTO;

/**
 * Author： roy
 * Description：
 **/
public interface IUserMobileRPCService {

    /**
     * 用户手机号登录(如果没有注册过，底层会自动完成注册)
     * @param mobile
     * @return
     */
    UserLoginDTO login(String mobile);
}
