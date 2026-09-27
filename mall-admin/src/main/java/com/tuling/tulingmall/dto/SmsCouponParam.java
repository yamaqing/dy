package com.tuling.tulingmall.dto;

import com.tuling.tulingmall.model.SmsCoupon;
import com.tuling.tulingmall.model.SmsCouponProductCategoryRelation;
import com.tuling.tulingmall.model.SmsCouponProductRelation;

import java.util.List;

import lombok.Data;
/**
 * 优惠券信息封装，包括绑定商品和绑定分类
 * Created on 2018/8/28.
 */
@Data
public class SmsCouponParam extends SmsCoupon {
    //优惠券绑定的商品
    private List<SmsCouponProductRelation> productRelationList;
    //优惠券绑定的商品分类
    private List<SmsCouponProductCategoryRelation> productCategoryRelationList;








}
