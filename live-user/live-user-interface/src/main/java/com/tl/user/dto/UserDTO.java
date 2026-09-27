package com.tl.user.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
/**
 * 用户数据实体
 */
@Data
public class UserDTO implements Serializable {

    private Long userId;
    private String nickName;

    private String trueName;
    private String avatar;

    private Integer sex;

    private Date createTime;
    private Date updateTime;

}
