package com.tuling.tulingmall.model;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
@Data
public class CmsTopic implements Serializable {
    private Long id;

    private Long categoryId;

    private String name;

    private Date createTime;

    private Date startTime;

    private Date endTime;

    @ApiModelProperty(value = "参与人数")
    private Integer attendCount;

    @ApiModelProperty(value = "关注人数")
    private Integer attentionCount;

    private Integer readCount;

    @ApiModelProperty(value = "奖品名称")
    private String awardName;

    @ApiModelProperty(value = "参与方式")
    private String attendType;

    @ApiModelProperty(value = "话题内容")
    private String content;

    private static final long serialVersionUID = 1L;

















































    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", name=").append(name);
        sb.append(", createTime=").append(createTime);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", attendCount=").append(attendCount);
        sb.append(", attentionCount=").append(attentionCount);
        sb.append(", readCount=").append(readCount);
        sb.append(", awardName=").append(awardName);
        sb.append(", attendType=").append(attendType);
        sb.append(", content=").append(content);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}