package com.tuling.tulingmall.portal.domain;

import com.tuling.tulingmall.model.PmsProduct;
import com.tuling.tulingmall.model.PmsProductFullReduction;
import com.tuling.tulingmall.model.PmsProductLadder;
import com.tuling.tulingmall.model.PmsSkuStock;

import java.util.List;

import lombok.Data;
/**
 * Created by tuling on 2018/8/27.
 * 商品的促销信息，包括sku、打折优惠、满减优惠
 */
@Data
public class PromotionProduct extends PmsProduct {
    //商品库存信息
    private List<PmsSkuStock> skuStockList;
    //商品打折信息
    private List<PmsProductLadder> productLadderList;
    //商品满减信息
    private List<PmsProductFullReduction> productFullReductionList;












}
