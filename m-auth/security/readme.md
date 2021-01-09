# 基于reactor的security jwt 配置

#### 前言

为了充分利用 Webflux 的吞吐和响应能力，为了更好的应用于分布式系统，所以放弃了一部分 Security 的能力，更好发挥reactor能力的同时还保留了security的大部分功能，所以更加灵活。

放弃的这部分功能，在分布式系统中几乎毫无用处


# 1  核心配置

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
#### 1.1 针对核心配置进行说明： 
 1 首先关闭security 自带的表单登录登出 和 开启跨域支持，我们用自己实现的reactor 登录 api进行登录
```java
 http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable);
```

2 security 灵活就在于它可以让你自由的配置各个授权过滤器
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
3 我们的授权过滤器
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
  3.1 ManageAuthenticationManager  自定义的授权过滤器
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
  从源码中我们可以看出 security 提供很多默认的过滤器，从源码filter方法中我们可以很直观的看出security 认证的执行顺序，我们要做的就是替换我们自己的过滤器进去，并且源码中也提供了构造方法和set方法，所有我们可以很容易的进行设置，然后再利用.addFilterAt 启用即可。
  
