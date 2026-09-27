package com.tuling.tulingmall.domain;

import com.tuling.tulingmall.model.PmsProduct;
import com.tuling.tulingmall.model.PmsProductAttribute;
import com.tuling.tulingmall.model.PmsSkuStock;

import java.util.List;

import lombok.Data;
/**
 * 购物车中选择规格的商品信息
 * Created by macro on 2018/8/2.
 */
@Data
public class CartProduct extends PmsProduct {
    private List<PmsProductAttribute> productAttributeList;
    private List<PmsSkuStock> skuStockList;








}
