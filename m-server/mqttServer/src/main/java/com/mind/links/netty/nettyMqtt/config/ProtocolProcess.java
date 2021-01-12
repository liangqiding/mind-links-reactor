package com.mind.links.netty.nettyMqtt.config;

import com.mind.links.netty.nettyMqtt.BrokerServer;
import com.mind.links.netty.nettyMqtt.COMMON.auth.IAuthService;
import com.mind.links.netty.nettyMqtt.COMMON.message.IDupPubRelMessageStoreService;
import com.mind.links.netty.nettyMqtt.COMMON.message.IDupPublishMessageStoreService;
import com.mind.links.netty.nettyMqtt.COMMON.message.IMessageIdService;
import com.mind.links.netty.nettyMqtt.COMMON.message.IRetainMessageStoreService;
import com.mind.links.netty.nettyMqtt.Connect;
import com.mind.links.netty.nettyMqtt.InternalCommunication;
import com.mind.links.netty.nettyMqtt.subscribe.SubscribeStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 协议处理
 */
@Component
public class ProtocolProcess {

    @Autowired
    private com.mind.links.netty.nettyMqtt.COMMON.session.SessionStoreServiceImpl SessionStoreServiceImpl;

    @Autowired
    private SubscribeStoreService subscribeStoreService;


    private IAuthService authService;


    private IMessageIdService messageIdService;


    private IRetainMessageStoreService messageStoreService;


    private IDupPublishMessageStoreService dupPublishMessageStoreService;


    private IDupPubRelMessageStoreService dupPubRelMessageStoreService;


    private InternalCommunication internalCommunication;


    private final BrokerProperties brokerProperties = new BrokerProperties();


    private Connect connect;


    public Connect connect() {
        if (connect == null) {
            connect = new Connect(SessionStoreServiceImpl, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authService, brokerProperties, BrokerServer.channelGroup, BrokerServer.channelIdMap);
        }
        return connect;
    }


}
