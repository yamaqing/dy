package com.tuling.tulingmall.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

import lombok.Data;
/**
 * 用户收藏的商品
 * Created by macro on 2018/8/2.
 */
@Data
public class MemberProductCollection {
    @Id
    private String id;
    @Indexed
    private Long memberId;
    private String memberNickname;
    private String memberIcon;
    @Indexed
    private Long productId;
    private String productName;
    private String productPic;
    private String productSubTitle;
    private String productPrice;
    private Date createTime;








































}
