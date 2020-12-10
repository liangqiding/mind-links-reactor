package com.mind.links.common.aopLog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mind.links.common.exception.LinksException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

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
//@Lazy(false)
public class CustomAspect {

    public CustomAspect() {
        logger.info("CustomAspect B 实例化=====================================");
    }

    private final Logger logger = LoggerFactory.getLogger(CustomAspect.class);

    @Pointcut("@annotation(com.mind.links.common.aopLog.CustomAopHandler)")
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
        HttpServletRequest request = this.getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        JSONObject parameter = JSON.parseObject(JSON.toJSONString(parameterMap));
        CustomAopHandler customAopHandler = getDeclaredAnnotation(joinPoint);
        if (customAopHandler.checkPage()) {
            // todo check page params
            try {
                if (!this.checkPage(parameter.getString("pageSize"), parameter.getString("pageNumber"))) {
                    throw new LinksException(20406, "请检查分页参数是否完整");
                }
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
            requestInfo.put("executionTime", (end - start) + "ms");
            logger.info("########" + requestInfo);
        }
        return proceed;
    }

    public JSONObject getRequestInfo(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        JSONObject requestInfo = new JSONObject();
        try {
            String url = request.getRequestURL().toString();
            String method = request.getMethod();
            String remoteIp = request.getRemoteAddr();
            String servletPath = request.getServletPath();
            String userId = request.getHeader("userId");
            Map<String, String[]> parameterMap = request.getParameterMap();
            JSONObject parameter = JSON.parseObject(JSON.toJSONString(parameterMap));
            requestInfo.put("user", userId);
            requestInfo.put("url", url);
            requestInfo.put("method", method);
            requestInfo.put("remoteIp", remoteIp);
            requestInfo.put("servletPath", servletPath);
            requestInfo.put("parameter", parameter);
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
        } catch (ClassNotFoundException e) {
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

    public HttpServletRequest getRequest() {
        HttpServletRequest request = null;
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            assert requestAttributes != null;
            request = (HttpServletRequest) requestAttributes
                    .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        } catch (Exception e) {
            logger.error("**************************request is null****************************************");
            throw new LinksException(20600, "请求不存在或不符合要求");
        }
        return request;
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
