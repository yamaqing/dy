package com.tl.id.generate;

import jakarta.annotation.Resource;
import me.ahoo.cosid.IdGenerator;
import me.ahoo.cosid.provider.IdGeneratorProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Author： roy
 * Description：
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class IDTest {
    @Resource
    private IdGeneratorProvider idGeneratorProvider;

    @Test
    public void idTest() {
        for (int i = 0; i < 10; i++) {
            //序列递增的SegmentID
            System.out.println("1==>"+idGeneratorProvider.get("tl-live-user").get().generate());
            //非序列递增的SnowflakeID
            System.out.println("2==>"+idGeneratorProvider.get("tl-live-order").get().generate());
            System.out.println("========");
        }

    }
}
