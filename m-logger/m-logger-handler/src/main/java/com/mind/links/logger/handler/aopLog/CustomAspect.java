package com.mind.links.logger.handler.aopLog;

import com.alibaba.fastjson.JSON;
import com.mind.links.common.exception.LinksException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * @author : qiDing
 * date: 2020-11-06 18:50
 * @version v1.0.0
 * description : TODO  Section processing
 * <p>
 * lazy Note: The container generally instantiates all single-instance beans at startup. If we want Spring to delay loading beans at startup, we need to use this annotation
 */

@Aspect
@Component
@Lazy(false)
@Slf4j
public class CustomAspect {

    @Value("${server.port:}")
    private String port;

    @Value("${netty.port:}")
    private String nettyPort;

    private static String IP;

    static {
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            IP = "Unknown";
        }
    }

    public CustomAspect() {
        log.info("=== com.mind.links.logger.handler.aopLog  实例化成功=====================================");
    }

    @Pointcut("@annotation(com.mind.links.logger.handler.aopLog.CustomAopHandler)")
    private void cutMethod() {
    }

    /**
     * Pre-notification: called before the target method is executed
     */
    @Before("cutMethod()")
    public void begin() {

    }

    /**
     * Post notification: Called after the target method is executed, if the target method is abnormal, it will not be executed
     */
    @AfterReturning("cutMethod()")
    public void afterReturning() {
    }

    /**
     * Post/final notification: no matter the target method appears in the execution process, it will be called after it
     */
    @After("cutMethod()")
    public void after() {

    }

    /**
     * Exception notification: executed when the target method throws an exception
     */
    @AfterThrowing(value = "cutMethod()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, RuntimeException ex) throws NoSuchMethodException {
        Mono.fromCallable(() -> this.getRequestInfo(((ProceedingJoinPoint) joinPoint)))
                .map(map0 -> map0.put("error", ex.getMessage()))
                .subscribe(map0 -> log.error(JSON.toJSONString(map0)));
    }

    /**
     * pageSize pageNumber
     * <p>
     * <p>
     * Surround notification: flexible and free to cut code into target methods
     */
    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final CustomAopHandler customAopHandler = getDeclaredAnnotation(joinPoint);
        final Map<String, Object> parameters = this.getParameters(joinPoint);
        final Map<String, Object> requestInfo = this.getRequestInfo(joinPoint, customAopHandler, parameters);
        // 是否检查分页参数
        if (customAopHandler.checkPage()) {
            this.checkPage(parameters.get("pageNumber").toString(), parameters.get("pageSize").toString());
        }
        long start = System.currentTimeMillis();
        Object proceed = ((ProceedingJoinPoint) joinPoint).proceed();
        long end = System.currentTimeMillis();
        // 是否生成日记
        if (customAopHandler.log()) {
            requestInfo.put("executionTime", (end - start) + "ms");
            log.info(JSON.toJSONString(requestInfo));
        }
        return proceed;
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/29 15:22
     * description：TODO 获取方法各种参数
     */
    public Map<String, Object> getRequestInfo(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        return this.getRequestInfo(joinPoint, this.getDeclaredAnnotation((ProceedingJoinPoint) joinPoint), getParameters(joinPoint));
    }

    public Map<String, Object> getRequestInfo(ProceedingJoinPoint joinPoint, CustomAopHandler customAopHandler) {
        return this.getRequestInfo(joinPoint, customAopHandler, getParameters(joinPoint));
    }

    public Map<String, Object> getRequestInfo(ProceedingJoinPoint joinPoint, CustomAopHandler customAopHandler, Map<String, Object> parameters) {
        Map<String, Object> requestMaps = getParameters(joinPoint);
        requestMaps.put("model", customAopHandler.module());
        requestMaps.put("desc", customAopHandler.desc());
        requestMaps.put("port", port + nettyPort);
        requestMaps.put("ip", IP);
        try {
            String targetName = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Class<?> targetClass = Class.forName(targetName);
            requestMaps.put("methodName", methodName);
            requestMaps.put("parameters", parameters);
            requestMaps.put("targetClass", targetClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestMaps;
    }

    /**
     * Gets the annotation declared in the method
     *
     * @param joinPoint ·
     * @return ·
     * @throws NoSuchMethodException ·
     */
    public CustomAopHandler getDeclaredAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);
        return objMethod.getDeclaredAnnotation(CustomAopHandler.class);
    }

    public Mono<ServerHttpRequest> getRequest() {
        return ReactiveRequestContextHolder.getRequest();
    }

    private void checkPage(String pageNumber, String pageSize) {
        if (pageNumber == null
                || ("").equals(pageNumber)
                || pageSize == null
                || ("").equals(pageSize)
                || !pageNumber.matches(".*[0-9].*")
                || !pageSize.matches(".*[0-9].*")) {
            throw new LinksException(30506, "请检查分页参数是否完整");
        }
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/29 14:59
     * description：TODO 获取请求参数
     */
    public Map<String, Object> getParameters(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        Map<String, Object> paramsMap = new HashMap<>(10);
        for (int i = 0; i < parameterValues.length; i++) {
            try {
                Object s = parameterValues[i];
                paramsMap.put(parameterNames[i], s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paramsMap;
    }

    @Configuration
    @ConditionalOnProperty(name = "port", prefix = "enable", havingValue = "true")
    public static class LinksEnvironment {

        public static void main(String[] args) {
            Object apply = ((Function<Object, Object>) o -> o).apply(66666);
            System.out.println(apply);
        }
    }
}
