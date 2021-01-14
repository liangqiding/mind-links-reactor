/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 内部消息
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class InternalMessage implements Serializable {

    private static final long serialVersionUID = -1L;

    private String brokerId;

    private String processId;

    private String clientId;

    private String topic;

    private int mqttQoS;

    private byte[] messageBytes;

    private boolean retain;

    private boolean dup;

}
