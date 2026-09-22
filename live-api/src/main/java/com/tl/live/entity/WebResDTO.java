package com.tl.live.entity;

/**
 * Author： roy
 * Description：统一封装前端响应结果
 **/
public class WebResDTO {

    //成功响应码
    public static final int SUCCESS_CODE=200;
    //失败响应码
    public static final int ERROR_CODE=500;

    private int code = 0;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public WebResDTO(int code) {
        this.code = code;
    }

    public WebResDTO(int code, Object data) {
        this.code = code;
        this.data = data;
    }
}
