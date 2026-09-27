package com.tuling.tulingmall.ordercurr.domain;

import com.tuling.tulingmall.ordercurr.model.UmsMemberReceiveAddress;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
/**
 * 确认单信息封装
 */
@Data
public class ConfirmOrderResult {
    //包含优惠信息的购物车信息
    private List<CartPromotionItem> cartPromotionItemList;
    //用户收货地址列表
    private List<UmsMemberReceiveAddress> memberReceiveAddressList;
//    //用户可用优惠券列表
//    private List<SmsCouponHistoryDetail> couponHistoryDetailList;
//    //积分使用规则
//    private UmsIntegrationConsumeSetting integrationConsumeSetting;
    //会员持有的积分
    private Integer memberIntegration;
    //计算的金额
    private CalcAmount calcAmount;









//    public List<SmsCouponHistoryDetail> getCouponHistoryDetailList() {
//        return couponHistoryDetailList;
//    }
//
//    public void setCouponHistoryDetailList(List<SmsCouponHistoryDetail> couponHistoryDetailList) {
//        this.couponHistoryDetailList = couponHistoryDetailList;
//    }
//
//    public UmsIntegrationConsumeSetting getIntegrationConsumeSetting() {
//        return integrationConsumeSetting;
//    }
//
//    public void setIntegrationConsumeSetting(UmsIntegrationConsumeSetting integrationConsumeSetting) {
//        this.integrationConsumeSetting = integrationConsumeSetting;
//    }









    @Data
    public static class CalcAmount{
        //订单商品总金额
        private BigDecimal totalAmount;
        //运费
        private BigDecimal freightAmount;
        //活动优惠
        private BigDecimal promotionAmount;
        //应付金额
        private BigDecimal payAmount;
















    }
}
