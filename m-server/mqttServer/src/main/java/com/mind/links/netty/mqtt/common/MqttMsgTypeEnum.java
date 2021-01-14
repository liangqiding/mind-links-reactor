package com.mind.links.netty.mqtt.common;

import com.mind.links.netty.mqtt.mqttHandler.protocolHandler.*;
import exception.LinksExceptionTcp;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;
import java.util.Arrays;


/**
 * date: 2021-01-13 11:44
 * description mqtt 报文类型枚举
 *
 * @author qiDing
 */
public enum MqttMsgTypeEnum {

    /**
     * 报文枚举
     */
    CONNECT(1, "连接请求") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return connectHandler.connect(channel, msg);
        }
    },
    CONN_ACK(2, "确认连接请求") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return connAckHandler.connAck(channel, msg);
        }
    },
    PUBLISH(3, "发布消息") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return publishHandler.publish(channel, msg);
        }
    },
    PUB_ACK(4, "发布确认") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return pubAckHandler.pubAck(channel, msg);
        }
    },
    PUB_REC(5, "发布已接受（qos2 第一步）") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return pubRecHandler.pubRec(channel, msg);
        }
    },
    PUB_REL(6, "发布已释放（qos2 第二步）") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return pubRelHandler.pubRel(channel, msg);
        }
    },
    PUB_COMP(7, "发布完成（qos2 第三步）") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return pubCompHandler.pubComp(channel, msg);
        }
    },
    SUBSCRIBE(8, "订阅请求") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return subscribeHandler.subscribe(channel, msg);
        }
    },
    SUB_ACK(9, "订阅确认") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return subAckHandler.subAck(channel, msg);
        }
    },
    UNSUBSCRIBE(10, "取消订阅") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return unsubscribeHandler.unsubscribe(channel, msg);
        }
    },
    UN_SUB_ACK(11, "取消订阅确认") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return unSubAckHandler.unSubAck(channel, msg);
        }
    },
    PING_REQ(12, "PING请求") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return pingRegHandler.pingReq(channel);
        }
    },
    PING_RESP(13, "PING响应") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return pingRespHandler.pingResp(channel);
        }
    },
    DISCONNECT(14, "断开通知") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return disconnectHandler.disconnect(channel);
        }
    };

    private final int value;

    public abstract Mono<Channel> msgHandler(Channel channel, final MqttMessage msg);

    MqttMsgTypeEnum(int value, String tag) {
        this.value = value;
    }

    public static Mono<Channel> msgHandler(int messageType, Channel channel, final MqttMessage msg) {
        return Flux.fromIterable(Arrays.asList(values().clone()))
                .log()
                .filter(e -> e.value == messageType)
                .switchIfEmpty(LinksExceptionTcp.errors("错误的消息类型"))
                .next()
                .flatMap(mqttMsgTypeEnum -> mqttMsgTypeEnum.msgHandler(channel, msg));

    }

    private static IMqttMessageHandler connectHandler;

    private static IMqttMessageHandler publishHandler;

    private static IMqttMessageHandler pubAckHandler;

    private static IMqttMessageHandler pubRecHandler;

    private static IMqttMessageHandler connAckHandler;

    private static IMqttMessageHandler pubRelHandler;

    private static IMqttMessageHandler pubCompHandler;

    private static IMqttMessageHandler subscribeHandler;

    private static IMqttMessageHandler subAckHandler;

    private static IMqttMessageHandler unsubscribeHandler;

    private static IMqttMessageHandler unSubAckHandler;

    private static IMqttMessageHandler pingRegHandler;

    private static IMqttMessageHandler pingRespHandler;

    private static IMqttMessageHandler disconnectHandler;

    @Component
    @RequiredArgsConstructor
    public static class ReportTypeServiceInjector {

        private final ConnectHandler connect;

        private final ConnAckHandler connAck;

        private final PublishHandler publish;

        private final PubAckHandler pubAck;

        private final PubRecHandler pubRec;

        private final PubRelHandler pubRel;

        private final PubCompHandler pubComp;

        private final SubscribeHandler subscribe;

        private final SubAckHandler subAck;

        private final UnsubscribeHandler unsubscribe;

        private final UnSubAckHandler unSubAck;

        private final PingRegHandler pingReg;

        private final PingRespHandler pingResp;

        private final DisconnectHandler disconnect;


        @PostConstruct
        public void postConstruct() {
            connectHandler = connect;
            connAckHandler = connAck;
            publishHandler = publish;
            pubAckHandler = pubAck;
            pubRecHandler = pubRec;
            pubRelHandler = pubRel;
            pubCompHandler = pubComp;
            subscribeHandler = subscribe;
            subAckHandler = subAck;
            unsubscribeHandler = unsubscribe;
            unSubAckHandler = unSubAck;
            pingRegHandler = pingReg;
            pingRespHandler = pingResp;
            disconnectHandler = disconnect;
        }
    }

}
