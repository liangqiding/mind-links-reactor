# 基于reactor的security jwt 授权服务器搭建

#### 前言

为了充分利用 Webflux 的吞吐和响应能力，并且更好的应用于分布式系统中，所以放弃了一部分 Security 的功能。本服务的目的是搭建一个授权服务器，更好发挥reactor能力的同时还能保留了security的大部分功能，并且使授权更加灵活。

# 1 需求分析

##### 1.1 登录功能
    首先我们得需要一个登录系统，帮助我们完成对账号密码的校验。

##### 1.2 jwt实现token认证
    在分布式系统中，内部会有很多服务，各个服务根据业务的需求往往会集群部署多个，security的session 登录机制，就不适合我们了。我们只需要security的认证功能，并整合jwt取代session.
	
##### 1.3 单点登录
    我们需要一次性登录后，就可以自由浏览内部的所有服务，通过权限去限制访问，而不是每切换一个服务就需要登录一次。这里我们选择整合gateway 网关进行统一认证，详情可以参考本项目的gateway服务。简单来讲就是 gateway作为服务对外的统一端点，所有请求都将由gateway进行统一拦截转发，把security作为认证服务器，通过调用security的jwt效验，完成授权。
   
##### 1.4 整合缓存实现token存储
    我们还需要把token和一些用户的登录状态存储进缓存中，比如token的刷新机制，access_token 及 refresh_token的实现等，更方便管理及强制离线用户等功能的实现，本服务选择整合redisson，redisson有较好的分布式支持，而且也支持响应式的编写方式。

##### 1.5 模拟session
    由于单机的session并不适合我们分布式的系统，我们需要构建一个分布式的session机制，以减轻授权服务器的压力，比如某用户登录成功后，下一次即可通过模拟session跳过验证，直接访问。
 

# 2  核心配置
本服务核心流程如图，这是我根据自己业务以及使用到的过滤器画的流程图

![系统架构图](https://gitee.com/liangqiding/mind-links-static/raw/master/security/flowChart.png)

> 用到的pom包  | [版本号参考](../../pom.xml)

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>0.9.1</version>
        </dependency>
        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
            <version>3.12.0</version>
        </dependency>
```

#### 2.1 针对核心配置进行说明： 
配置代码：
```java
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityWebFluxConfiguration {

    private final TokenProvider tokenProvider;

    private final FailureHandler failureHandler;

    private final ManageAuthenticationManager manageAuthenticationManager;

    private final UnauthorizedAuthenticationEntryPoint unauthorizedAuthenticationEntryPoint;

    @ApiModelProperty("白名单")
    private static final String[] AUTH_WHITELIST = {"/user/**","/api/**","/auth/**"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 关闭自带的登录 我们用自己实现的 reactor api 进行登录
     */
    @Bean
    public SecurityWebFilterChain userSecurityFilterChain(ServerHttpSecurity http) {
        http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable);

        http
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedAuthenticationEntryPoint))
                .securityMatcher(ServerWebExchangeMatchers.anyExchange())
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(a -> a.pathMatchers(HttpMethod.OPTIONS).permitAll())
                .authorizeExchange(n -> n.pathMatchers(AUTH_WHITELIST).permitAll().anyExchange().authenticated());

        return http.build();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter() {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(manageAuthenticationManager);
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(new JwtHeadersExchangeMatcher());
        bearerAuthenticationFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter(tokenProvider));
        bearerAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);
        return bearerAuthenticationFilter;
    }

}
```

#####  2.1.1 首先关闭security 自带的表单登录登出 和 开启跨域支持，我们用自己实现的reactor 登录 api进行登录
```java
 http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable);
```

##### 2.1.2 security 灵活就在于它可以让你自由的配置各个授权过滤器
```java
http
                 // 授权失败处理  不配做默认返回空白的 401响应
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedAuthenticationEntryPoint))
                .securityMatcher(ServerWebExchangeMatchers.anyExchange())
				// 添加核心认证类
                .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
				// 开放跨域的预检请求
                .authorizeExchange(a -> a.pathMatchers(HttpMethod.OPTIONS).permitAll())
				// 添加我们默认放行的请求
                .authorizeExchange(n -> n.pathMatchers(AUTH_WHITELIST).permitAll().anyExchange().authenticated());
```
##### 2.1.3 我们的授权过滤器
```java
 private AuthenticationWebFilter bearerAuthenticationFilter() {
        // 1 创建我们自定义的授权过滤器 
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(manageAuthenticationManager);
		// 2 配置需要身份验证匹配器 这里作用是区分token校验的请求，不带token 且非开放的api 直接返回未授权
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(new JwtHeadersExchangeMatcher());
		// 3 令牌认证转换器 
        bearerAuthenticationFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter(tokenProvider));
		// 4 认证错误处理器
        bearerAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);
        return bearerAuthenticationFilter;
    }
```
说明： 
#####   2.1.4 ManageAuthenticationManager  自定义的授权过滤器
  首先我们看看security 的源码
  源码：
  ```java
  public class AuthenticationWebFilter implements WebFilter {
    private final ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver;
    private ServerAuthenticationSuccessHandler authenticationSuccessHandler = new WebFilterChainServerAuthenticationSuccessHandler();
    private ServerAuthenticationConverter authenticationConverter = new ServerHttpBasicAuthenticationConverter();
    private ServerAuthenticationFailureHandler authenticationFailureHandler = new ServerAuthenticationEntryPointFailureHandler(new HttpBasicServerAuthenticationEntryPoint());
    private ServerSecurityContextRepository securityContextRepository = NoOpServerSecurityContextRepository.getInstance();
    private ServerWebExchangeMatcher requiresAuthenticationMatcher = ServerWebExchangeMatchers.anyExchange();

    public AuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        this.authenticationManagerResolver = (request) -> {
            return Mono.just(authenticationManager);
        };
    }

    public AuthenticationWebFilter(ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver) {
        Assert.notNull(authenticationManagerResolver, "authenticationResolverManager cannot be null");
        this.authenticationManagerResolver = authenticationManagerResolver;
    }

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return this.requiresAuthenticationMatcher.matches(exchange).filter((matchResult) -> {
            return matchResult.isMatch();
        }).flatMap((matchResult) -> {
            return this.authenticationConverter.convert(exchange);
        }).switchIfEmpty(chain.filter(exchange).then(Mono.empty())).flatMap((token) -> {
            return this.authenticate(exchange, chain, token);
        }).onErrorResume(AuthenticationException.class, (e) -> {
            return this.authenticationFailureHandler.onAuthenticationFailure(new WebFilterExchange(exchange, chain), e);
        });
    }
	... 省略
  ```
  从源码中我们可以看出 security 提供很多默认的过滤器，从源码filter方法中我们可以很直观的看出security 认证的执行顺序，我们要做的就是替换我们自己的过滤器进去，并且源码中也提供了构造方法和set方法，所有我们可以很容易的进行设置，然后再利用.addFilterAt 添加过滤器即可。
  
# 3 根据需求实现功能

#####  3.1 实现登录功能
我们参考security的源码，实现一个响应式的登录服务

首先我们得先配置一个 ManageAuthenticationManager 进行账号密码校验，从上面的流程图中可知，ManageAuthenticationManager 属于认证流程最后一步（在流程图中）。

> 我们实现的ManageAuthenticationManager

  ```java
@Component
@Primary
@Slf4j
public class ManageAuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {

    private final static Scheduler SCHEDULER = Schedulers.boundedElastic();

    @Autowired
    private ManageDetailsServiceImpl manageDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        logger.debug("authentication:" + JSON.toJSONString(authentication));
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }
        return retrieveUser(authentication.getName())
                .publishOn(SCHEDULER)
                .filter(u -> passwordEncoder.matches(String.valueOf(authentication.getCredentials()), u.getPassword()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("账号或密码错误！"))))
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(),u.getAuthorities()));
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        return manageDetailsService.findByUsername(username);
    }
}
  ```


    我们需要继承 AbstractUserDetailsReactiveAuthenticationManager 重写 authenticate 方法，编写我们自己的逻辑，再通过security的配置替换默认的ManageAuthenticationManager。
    我们可看到这个方法传入的是一个authentication 返回一个 Mono<Authentication>。所有我们要调用这个方法，得先有一个authentication对象。authentication对象可以通过 new UsernamePasswordAuthenticationToken(username, u.getPassword(),u.getAuthorities()) 获得。

>  接下来我们就可以写我们的登录服务了
------------

  ```java

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthHandler {

    private final TokenProvider tokenProvider;

    private final ManageAuthenticationManager manageAuthenticationManager;

    public Mono<String> loginAuth(String username, String password,String clientId) {
        log.debug("clientId:"+clientId);
        return manageAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .map(auth -> {
                    log.debug(JSON.toJSONString(auth));
                    ReactiveSecurityContextHolder.withAuthentication(auth);
                    return tokenProvider.createToken(auth,clientId);
                });
    }

}

```
# 总结
  其它功能的实现这里就不写了，各个过滤器根据自己的业务进行编写即可，如jwt等其它实现可以参考本项目代码，代码有完整的备注及解释



