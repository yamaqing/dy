package com.tl.id.generate.impl;

import com.tl.id.generate.util.Constants;
import com.tl.id.inter.IGenerateIDRPCService;
import jakarta.annotation.Resource;
import me.ahoo.cosid.provider.IdGeneratorProvider;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * Author： roy
 * Description：
 **/
@DubboService
public class GenerateIDRPCService implements IGenerateIDRPCService {

    @Resource
    private IdGeneratorProvider idGeneratorProvider;

    @Override
    public Long getSeqId() {
        return idGeneratorProvider.get(Constants.ID_SEQUENCE).get().generate();
    }

    @Override
    public Long getUnSeqId() {
        return idGeneratorProvider.get(Constants.ID_SNOWFLAKE).get().generate();
    }
}
