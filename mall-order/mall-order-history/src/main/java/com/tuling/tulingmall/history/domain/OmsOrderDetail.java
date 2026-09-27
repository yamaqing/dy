package com.tuling.tulingmall.history.domain;


import com.tuling.tulingmall.history.model.OmsOrder;
import com.tuling.tulingmall.history.model.OmsOrderItem;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import lombok.Data;
/**
 * 包含订单商品信息的订单详情
 */
@Data
@Document("orderhistory")
public class OmsOrderDetail extends OmsOrder {
    private List<OmsOrderItem> orderItemList;




}
