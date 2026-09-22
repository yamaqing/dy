package com.tl.id.inter;

public interface IGenerateIDRPCService {
    /**
     * 获取有序id
     *
     * @return
     */
    Long getSeqId();

    /**
     * 获取无序id
     *
     * @return
     */
    Long getUnSeqId();
}
