/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.mind.links.netty.nettyMqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 消息转发，基于kafka
 */
@Component
@Slf4j
public class InternalCommunication {

    public void internalSend(InternalMessage internalMessage) {
         log.info("internalSend="+internalMessage);
    }
}
