package com.tl.user.provider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;
/**
 * Author： roy
 * Description： 数据库对应实体
 **/
@Data
@TableName("t_user")
public class UserDO {

    @TableId(type = IdType.INPUT)
    private Long userId;
    private String nickName;
    private String trueName;
    private String avatar;
    private Integer sex;
    private Date createTime;
    private Date updateTime;

}
