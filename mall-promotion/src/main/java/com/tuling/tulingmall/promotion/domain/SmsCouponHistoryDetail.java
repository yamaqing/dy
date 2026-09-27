package com.tuling.tulingmall.promotion.domain;

import com.tuling.tulingmall.promotion.model.SmsCoupon;
import com.tuling.tulingmall.promotion.model.SmsCouponHistory;
import com.tuling.tulingmall.promotion.model.SmsCouponProductCategoryRelation;
import com.tuling.tulingmall.promotion.model.SmsCouponProductRelation;

import java.util.List;

import lombok.Data;
/**
 * 优惠券领取历史详情封装
 * Created by macro on 2018/8/29.
 */
@Data
public class SmsCouponHistoryDetail extends SmsCouponHistory {
    //相关优惠券信息
    private SmsCoupon coupon;
    //优惠券关联商品
    private List<SmsCouponProductRelation> productRelationList;
    //优惠券关联商品分类
    private List<SmsCouponProductCategoryRelation> categoryRelationList;












}
