package com.mind.links.logger.handler.aopLog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mind.links.common.exception.LinksException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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
public class CustomAspect {

    @Value("${server.port}")
    private String port;


    public CustomAspect() {
        logger.info("=== com.mind.links.logger.handler.aopLog  实例化成功=====================================");
    }

    private final Logger logger = LoggerFactory.getLogger(CustomAspect.class);

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
    public void afterThrowing(JoinPoint joinPoint, RuntimeException ex) {
        System.out.println("===========================================================Throwing==========================================================");
        String message = ex.getMessage();
        JSONObject requestInfo = this.getRequestInfo(((ProceedingJoinPoint) joinPoint), this.getRequest());
        requestInfo.put("error", ex.getMessage());
        logger.error("****" + requestInfo);
    }

    /**
     * pageSize pageNumber
     * <p>
     * <p>
     * Surround notification: flexible and free to cut code into target methods
     */
    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Mono<ServerHttpRequest> request = this.getRequest();
//        AtomicReference<MultiValueMap<String, String>> q = null;
//        request.subscribe(req -> q.set(req.getQueryParams()));
        CustomAopHandler customAopHandler = getDeclaredAnnotation(joinPoint);
//        MultiValueMap<String, String> parameter = q.get();
        request.map(serverHttpRequest -> {
            System.out.println(serverHttpRequest.getURI().toString());
            return serverHttpRequest;
        });
        if (customAopHandler.checkPage()) {
            // todo check page params
            try {
//                if (!this.checkPage(parameter.getFirst("pageSize"), parameter.getFirst("pageNumber"))) {
//                    throw new LinksException(20406, "请检查分页参数是否完整");
//                }
            } catch (Exception e) {
                throw new LinksException(20406, "请检查分页参数是否完整");
            }
        }
        long start = System.currentTimeMillis();
        Object proceed = ((ProceedingJoinPoint) joinPoint).proceed();
        long end = System.currentTimeMillis();
        if (customAopHandler.log()) {
            // todo aop log start
            JSONObject requestInfo = this.getRequestInfo(joinPoint, request);
            requestInfo.put("model", customAopHandler.module());
            requestInfo.put("desc", customAopHandler.desc());
            requestInfo.put("port", port);
            requestInfo.put("executionTime", (end - start) + "ms");
            logger.info("########" + requestInfo);
        }
        return proceed;
    }

    public JSONObject getRequestInfo(ProceedingJoinPoint joinPoint, Mono<ServerHttpRequest> request) {

        JSONObject requestInfo = new JSONObject();
        try {
            request.map(req -> {
                String url = req.getURI().toString();
                String method = Objects.requireNonNull(req.getMethod()).name();
                String remoteIp = Objects.requireNonNull(req.getRemoteAddress()).getAddress().getHostAddress();
                MultiValueMap<String, String> queryParams = req.getQueryParams();
                String userId = req.getHeaders().getFirst("userId");
                requestInfo.put("user", userId);
                requestInfo.put("url", url);
                requestInfo.put("method", method);
                requestInfo.put("remoteIp", remoteIp);
                requestInfo.put("parameter", queryParams);
                return req;
            });
            //   aop  方法执行情况
            JSONObject aopAround = new JSONObject();
            String targetName = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] arguments = joinPoint.getArgs();
            try {
                JSON.toJSONString(arguments);
            } catch (Exception e) {
                arguments = null;
            }
            Class<?> targetClass = Class.forName(targetName);
            Method[] methods = targetClass.getMethods();
            aopAround.put("methodName", methodName);
            aopAround.put("arguments", arguments);
            aopAround.put("targetClass", targetClass);
            requestInfo.put("aopAround", aopAround);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestInfo;
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
        try {
            return ReactiveRequestContextHolder.getRequest();
        } catch (Exception e) {
            logger.error("**************************request is null****************************************");
            throw new LinksException(20600, "请求不存在或不符合要求");
        }
    }

    private boolean checkPage(String pageNumber, String pageSize) {
        return pageNumber != null
                && !("").equals(pageNumber)
                && pageSize != null
                && !("").equals(pageSize)
                && pageNumber.matches(".*[0-9].*")
                && pageSize.matches(".*[0-9].*");
    }
}
