package com.tuling.tulingmall.ordercurr.domain;

import lombok.Data;

@Data
public class StockChanges {

    private Long productSkuId;

    private Integer changesCount;









    public StockChanges(){}

    public StockChanges(Long productSkuId, Integer changesCount) {
        this.productSkuId = productSkuId;
        this.changesCount = changesCount;
    }
}
