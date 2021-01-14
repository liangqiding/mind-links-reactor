package com.mind.links.netty.mqtt.common;

import com.mind.links.netty.mqtt.mqttHandler.protocolHandler.ConnectHandler;
import com.mind.links.netty.mqtt.mqttHandler.protocolHandler.IMqttMessageHandler;
import com.mind.links.netty.mqtt.mqttHandler.protocolHandler.PingRegHandler;
import com.mind.links.netty.mqtt.mqttHandler.protocolHandler.PublishHandler;
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
            return Mono.empty();
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
            return Mono.empty();
        }
    },
    PUB_REC(5, "发布已接受（qos2 第一步）") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return Mono.empty();
        }
    },
    PUB_REL(6, "发布已释放（qos2 第二步）") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return Mono.empty();
        }
    },
    PUB_COMP(7, "发布完成（qos2 第三步）") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return Mono.empty();
        }
    },
    SUBSCRIBE(8, "订阅请求") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return Mono.empty();
        }
    },
    SUB_ACK(9, "订阅确认") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return Mono.empty();
        }
    },
    UNSUBSCRIBE(10, "取消订阅") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return Mono.empty();
        }
    },
    UN_SUB_ACK(11, "取消订阅确认") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return Mono.empty();
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
            return Mono.empty();
        }
    },
    DISCONNECT(14, "断开通知") {
        @Override
        public Mono<Channel> msgHandler(Channel channel, MqttMessage msg) {
            return Mono.empty();
        }
    };

    private final int value;

    public abstract Mono<Channel> msgHandler(Channel channel, final MqttMessage msg);

    MqttMsgTypeEnum(int value, String tag) {
        this.value = value;
    }

    public static Mono<Channel> msgHandler(int messageType, Channel channel, final MqttMessage msg) {
        return Flux.fromIterable(Arrays.asList(values().clone()))
                .filter(e -> e.value == messageType)
                .switchIfEmpty(LinksExceptionTcp.errors("错误的消息类型"))
                .log()
                .next()
                .flatMap(mqttMsgTypeEnum -> mqttMsgTypeEnum.msgHandler(channel, msg));

    }

    private static IMqttMessageHandler connectHandler;

    private static IMqttMessageHandler pingRegHandler;

    private static IMqttMessageHandler publishHandler;

    @Component
    @RequiredArgsConstructor
    public static class ReportTypeServiceInjector {

        private final ConnectHandler c;

        private final PingRegHandler pr;

        private final PublishHandler pl;

        @PostConstruct
        public void postConstruct() {
            connectHandler = c;
            pingRegHandler = pr;
            publishHandler = pl;
        }
    }

}
