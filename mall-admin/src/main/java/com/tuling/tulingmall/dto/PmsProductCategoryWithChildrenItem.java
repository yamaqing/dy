package com.tuling.tulingmall.dto;

import com.tuling.tulingmall.model.PmsProductCategory;

import java.util.List;

import lombok.Data;
/**
 * Created on 2018/5/25.
 */
@Data
public class PmsProductCategoryWithChildrenItem extends PmsProductCategory {
    private List<PmsProductCategory> children;




}
