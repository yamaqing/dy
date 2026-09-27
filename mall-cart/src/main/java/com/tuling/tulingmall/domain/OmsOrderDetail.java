package com.tuling.tulingmall.domain;

import com.tuling.tulingmall.model.OmsOrder;
import com.tuling.tulingmall.model.OmsOrderItem;

import java.util.List;

import lombok.Data;
/**
 * 包含订单商品信息的订单详情
 * Created by macro on 2018/9/4.
 */
@Data
public class OmsOrderDetail extends OmsOrder {
    private List<OmsOrderItem> orderItemList;




}
