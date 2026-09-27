package com.tuling.tulingmall;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.tuling.tulingmall.mapper.SmsCouponMapper;
import com.tuling.tulingmall.mapper.UmsAdminMapper;
import com.tuling.tulingmall.model.SmsCoupon;
import com.tuling.tulingmall.model.UmsAdmin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;

/**
 * @author roy
 * @desc
 */
@SpringBootTest
public class MapperTest {

    @Autowired
    private UmsAdminMapper adminMapper;

    @Autowired
    private SmsCouponMapper smsCouponMapper;

    @Test
    public void getUser(){
        List<UmsAdmin> umsAdmins = adminMapper.selectList(null);
        umsAdmins.forEach(System.out::println);
    }

    @Test
    public void getCoupon(){
        List<SmsCoupon> smsCoupons = smsCouponMapper.selectList(null);
        smsCoupons.forEach(System.out::println);
    }
}
