# 分布式日记系统搭建

#### 前言
   本服务的目的是完成对各种服务组件的日记进行收集，我们需要一个公共的日记生成处理及收集服务。


# 1 需求分析

#### 1.1 aop日记生成
 
 我们利用spring aop 实现一个通过注解拦截方法，利用环绕通知，达到日记生成的目的。

#### 1.2 日记收集

  这里我们选择将日记统一往kafka进行发送，再由一个日记消费服务从kafka中取出进行消费整理。这里的消费服务就比较多了，如Logstash和Kibana Elasticsearch 经典的ekf日记收集
	
#### 1.3 动态选择收集的日记级别
    
  对于日记，往往数据量是比较大的，很多的日记如debug并不是我们想收集的，徒增系统的资源耗费，所有我们需要一个可以动态配置日记收集级别的功能。
   
#### 1.4 以pom依赖的形式供其它服务使用

   对于效率而言，要是每个服务都配置一个日记收集程序，显然徒增很多无用重复的代码，所有这里选择制作成pom依赖，供其它服务导入使用。

# 2 核心配置

####  2.1 pom依赖的形式自动加载bean
>   2.1.1 配置spring.factories文件

  我们要实现pom 的形式供其它服务使用，首先得解决bean加载问题，spring默认是不会加载jar包里面的bean的，我们需要告诉spring我们要加载这些bean。
  关于加载bean，spring有很多种实现，这里我们选择直接在jar包里面配置META-INF/spring.factories 文件加载bean
  spring.factories文件内容：
  
  ```xml
  # Auto Configure factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.mind.links.logger.handler.config.LinksCommonClientConfig
  ```
  
 这样当其它服务引用该包的时候，就会默认加载jar里的LinksCommonClientConfig.class 类了
 
> 2.1.2 通过LinksCommonClientConfig.class 加载其它bean

  ```java
@Configuration
@ConditionalOnClass(CustomAspect.class)
@ComponentScan(basePackages = {"com.mind.links.logger.handler.aopLog"})
public class LinksCommonClientConfig {

}
```
加载jar里的com.mind.links.logger.handler.aopLog包

这样我们的pom依赖包就制作完成了。


# 3 切面配置
> 用到的pom包  | [版本号参考](../../pom.xml)

```xml
        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
        </dependency>
         <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
        </dependency>
        <dependency>
            <groupId>org.codehaus.janino</groupId>
            <artifactId>janino</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.danielwegener</groupId>
            <artifactId>logback-kafka-appender</artifactId>
        </dependency>

```



>  2.1.1 我们创建一个触发注解

 ```java
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface CustomAopHandler {

    /**
     * log ## Universal log printing in any scenario
     */
    boolean log() default true;

    /**
     * checkPage ## Paging parameter validation ( pageNumber pageSize )
     */
    boolean checkPage() default false;

    /**
     * module
     */
    String module() default "";

    /**
     * desc
     */
    String desc() default "";

}
  ```

> 2.1.2 切面逻辑处理

   ```java
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
        // 是否生成日记
        这里通过joinPoint切点获取方法各种参数进行日记打印即可，代码参考项目代码
		 Object proceed = ((ProceedingJoinPoint) joinPoint).proceed();
        return proceed;
    }

```
这里通过public Object around()方法进行日记捕获生成即刻，具体逻辑参考本项目代码

# 4 logback 配置


# 总结

