package com.tuling.tulingmall;

import com.tuling.tulingmall.mapper.OmsCartItemMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author roy
 * @desc
 */
@SpringBootTest
public class MapperTest {

    @Autowired
    private OmsCartItemMapper cartItemMapper;

    @Test
    public void CartItemTest(){
        System.out.println(cartItemMapper.selectByPrimaryKey(12L));
    }
}
