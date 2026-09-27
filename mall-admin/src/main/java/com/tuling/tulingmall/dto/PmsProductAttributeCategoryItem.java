package com.tuling.tulingmall.dto;

import com.tuling.tulingmall.model.PmsProductAttribute;
import com.tuling.tulingmall.model.PmsProductAttributeCategory;

import java.util.List;

import lombok.Data;
/**
 * 包含有分类下属性的dto
 * Created on 2018/5/24.
 */
@Data
public class PmsProductAttributeCategoryItem extends PmsProductAttributeCategory {
    private List<PmsProductAttribute> productAttributeList;




}
