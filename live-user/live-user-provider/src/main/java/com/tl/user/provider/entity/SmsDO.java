package com.tl.user.provider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * Author： roy
 * Description：短信记录表
 **/
@TableName("t_sms")
public class SmsDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer code;
    private String phone;
    private Date sendTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "SmsDO{" +
                "id=" + id +
                ", code=" + code +
                ", phone='" + phone + '\'' +
                ", sendTime=" + sendTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
