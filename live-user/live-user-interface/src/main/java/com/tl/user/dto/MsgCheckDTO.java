package com.tl.user.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
/**
 * Author： roy
 * Description：
 **/
@Data
public class MsgCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3394248744287019717L;
    private boolean checkStatus;
    private String desc;

    public MsgCheckDTO(boolean checkStatus, String desc) {
        this.checkStatus = checkStatus;
        this.desc = desc;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

}
