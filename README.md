# mind-links 基于Reactor3 netty的大型响应式分布式物联网智能家电系统,百万长连接

# mind-links 基于Reactor3 netty的大型响应式分布式物联网智能家电系统,百万长连接

## 前言

`mind-links` 本项目基于netty的大型响应式分布式物联网智能家电系统，主要技术包括：mqtt,Reactor3,netty,SpringCloud,nacos,Elasticsearch,Kafka,docker,Redisson,mysql,mongodb,EKL

> 项目包含前端web页面,后端web服务,tcp服务器
> 整合mqtt协议支持，百万长连接设计方案

## 相关项目
- mind-links后端项目
 项目地址地址： [https://github.com/liangqiding/mind-links](https://github.com/liangqiding/mind-links)

- mind-links TCP服务器基于netty的tcp长连接服务器

 项目地址地址： [https://github.com/liangqiding/mind-links-server](hhttps://github.com/liangqiding/mind-links-server)

- mind-links -前端项目 采用TS.JS形式
 项目地址地址： [https://github.com/liangqiding/mind-links-web](https://github.com/liangqiding/mind-links-web)

- mind-links -TCP测试设备 基于c/c++ 编写的小型测试客户端 

 目前项目还在持续更新中
 
## 项目文档

- 文档地址：

|  服务              | 文档                 |   
|-------------------|----------------------|
| m-security        |  [security登录服务文档...](./m-auth/security/readme.md)                |   
| m-logger          |  [全局日记收集文档...](./m-logger/m-logger-handler/readme.md)           |  

- 备用地址：[http://#暂无](https://#)

## 项目介绍

`mind-links`项目是一套分布式的智能家电系统，包括前台监控系统及后台管理系统,采用多种流行技术实现,核心技术如：netty,springCloud,nacos,采用Docker容器化部署。
### 架构图


##### 系统架构图
![系统架构图](https://gitee.com/liangqiding/mind-links-static/raw/master/drawio/systemStructureChart.png)

##### 业务架构图

![业务架构图](https://gitee.com/liangqiding/mind-links-static/raw/master/drawio/BusinessStructureChart.png)

#### 模块介绍
#### 后台模块

|  服务       | 服务说明       |   进度         |    备注   |
|------------|---------------|---------------|-----------|
| m-common |   公共组件  |    ✅          |    公用组件包       |
| m-auth   |   授权认证  |    ✅           |   登录认证，授权，token管理    |
| m-server |  TCP核心服务  |    🏗          |     TCP核心服务      |
| m-center  |总线控制测试中心   |    🏗           |   各种测试集合,程序总监控中心       |
| m-search |  ES 搜索框架  |    🏗           |       Elasticsearch 搜索引擎    |
| m-logger | 分布式日记收集  |    🏗           |   设计多模块共用自定义注解，并引入kafka日记统一收集，其它模块导入添加注解即可实现日记生成和收集，实现分布式日记收集分析        |
| m-gateway |  网关统计    |    🏗           |     网关实现多模块统计授权认证，分流及服务熔断      |


### 前端模块
|  服务     | 使用技术   |   进度         |    备注   |
|----------|-----------|---------------|-----------|
|   设计中  |           |              |           |


### 平台功能

|  服务     | 使用技术     |   进度         |    备注   |
|----------|-------------|---------------|-----------|
|  用户管理 | 略           |   设计中          |  用户是系统操作者，该功能主要完成系统用户配置。          |
|  设备管理 | 略           |   设计中          |  用户管理设备，对设备数据范围权限划分。                 |
|  权限管理 | 略           |   设计中           |  配置权限管理系统，操作权限，给用户分配权限。           |
|  机构管理 | 略           |    设计中           |  配置系统组织机构，树结构展现，可随意调整上下级。        |
|  网关动态路由 | 略        |   设计中           |  网关动态路由管理                                  |

### 开发运维

|  服务     | 使用技术                 |   进度         |    备注   |
|----------|-------------------------|---------------|-----------|
|  代码生成 |                         |   ✅          |  后端代码的生成         |
|  测试管理 |                         |   🏗          |           |
|  文档管理 | Swagger3                |   ✅          |           |
|  服务监控 | Spring Boot Admin       |   ✅          |           |
|  链路追踪 | SkyWalking              |   ✅          |           |
|  操作审计 |                         |   🏗          |  系统关键操作日志记录和查询         |
|  日志管理 | ES + Kibana、Zipkin     |   ✅          |           |
|  监控告警 | Grafana                 |   ✅          |           |


### 项目演示

#### 后台管理系统

前端项目`更新中`地址：https://github.com/liangqiding/xxx

项目演示地址(更新中)： [http://#](http://#)  

![后台监控系统功能演示(更新中)](http://#)

#### 前台监控仪表盘系统

前端项目`web`地址：敬请期待......

项目演示地址：[http://#](http://#)

![前台仪表盘系统功能演示](http://#)

### 技术选型

#### 后端技术

| 技术                  | 版本         |说明                | 官网                                                 |
| -------------------- | ---------   |------------------- | ---------------------------------------------------- |
| SpringCloud          |  Hoxton.SR5 |微服务容器           | https://spring.io/projects/spring-cloud             |
| SpringBoot           |2.3.3.RELEASE|SpringBoot容器       | https://spring.io/projects/spring-boot               |
| webFlux              |2.3.3.RELEASE|响应式编程           | https://github.com/reactor/reactor-core               |
| reactor-core         |3.3.9.RELEASE|reactor核心          | https://github.com/reactor/reactor-core               |
| SpringSecurity       |             |认证和授权框架       | https://spring.io/projects/spring-security           |
| mybatis-plus         |   3.3.1     |ORM框架             |                                                     |
| mybatis-plus-generator|  3.3.1     |数据层代码生成       | http://www.mybatis.org/generator/index.html          |
| PageHelper           |   1.2.13    |MyBatis物理分页插件 | http://git.oschina.net/free/Mybatis_PageHelper       |
| Swagger-UI           |   3.0.0     |文档生产工具        | https://github.com/swagger-api/swagger-ui            |
| Hibernator-Validator | 6.1.5.Final |验证框架            | http://hibernate.org/validator                       |
| Elasticsearch        |   7.10.1    |搜索引擎            | https://github.com/elastic/elasticsearch             |
| Kafka                |   2.5.x     |消息队列            |                                                      |
| Redisson             |   3.14.0    |分布式缓存,分布式锁   | https://redis.io/                                    |
| MongoDB              |    4.4      |NoSql数据库         | https://www.mongodb.com                              |
| Docker               |             |应用容器引擎        | https://www.docker.com                               |
| Druid                |   1.2.3     | 数据库连接池        | https://github.com/alibaba/druid                     |
| MinIO                |    8.x        |对象存储            | https://github.com/minio/minio                       |
| JWT                  |             |JWT登录支持         | https://github.com/jwtk/jjwt                         |
| LogStash             |   7.10.1    |日志收集工具        | https://github.com/logstash/logstash-logback-encoder |
| Lombok               |             |简化对象封装工具    | https://github.com/rzwitserloot/lombok               |
| Jenkins              |             |自动化部署工具      | https://github.com/jenkinsci/jenkins                 |

#### 前端技术

| 技术       | 说明                  | 官网                                   |
| ---------- | --------------------- | -------------------------------------- |
| Node       | 核心引擎             | https://nodejs.org/zh-cn/                    |
| ts.js      | 核心语言             | https://www.tslang.cn/                   |
| Vue        | 前端框架              | https://vuejs.org/                     |
| Vue-router | 路由框架              | https://router.vuejs.org/              |
| Vuex       | 全局状态管理框架      | https://vuex.vuejs.org/                |
| Element    | 前端UI框架            | https://element.eleme.io               |
| Axios      | 前端HTTP框架          | https://github.com/axios/axios         |
| v-charts   | 基于Echarts的图表框架 | https://v-charts.js.org/               |
| Js-cookie  | cookie管理工具        | https://github.com/js-cookie/js-cookie |
| nprogress  | 进度条控件            | https://github.com/rstacruz/nprogress  |


### 开发工具

| 工具          | 说明                | 官网                                            |
| ------------- | ------------------- | ----------------------------------------------- |
| IDEA 2020     | 开发IDE             | https://www.jetbrains.com/idea/download         |
| RedisDesktop  | redis客户端连接工具 | https://github.com/qishibo/AnotherRedisDesktopManager  |
| Robo 3T       | mongo客户端连接工具 | https://robomongo.org/download                  |
| X-shell       | Linux远程连接工具   | http://www.netsarang.com/download/software.html |
|Navicat Premium15 | 数据库连接工具      | http://www.navicat.com.cn/navicat-15-highlights/      |
|Navicat Premium15| 数据库设计工具      |                                               |
| draw.io         | 流程图设计工具        | https://drawio-app.com/                          |
| ApiFox        | 已弃用postman,变更为ApiFox,API接口调试工具      | https://www.apifox.cn/                        |
| VS code       | 做为Markdown编辑器      | https://code.visualstudio.com/                              |

### 开发环境

| 工具          | 版本号 | 下载                                                         |
| ------------- | ------ | ------------------------------------------------------------ |
| JDK           | 1.8    | https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html |
| Mysql         | 8.0    | https://www.mysql.com/                                       |
| Redis         |        | https://redis.io/download                                    |
| MongoDB       | 4.3    | https://www.mongodb.com/download-center                      |
| Kafka         |        | http://www.rabbitmq.com/download.html                        |
| Nginx         | 1.19.x | http://nginx.org/en/download.html                            |
| Elasticsearch | 7.10.1  | https://www.elastic.co/downloads/elasticsearch               |
| Logstash      | 7.10.1  | https://www.elastic.co/cn/downloads/logstash                 |
| Kibana        | 7.10.1  | https://www.elastic.co/cn/downloads/kibana                   |

### 搭建步骤

### 编译 & 启动

### 开发阶段暂不提供docker-compos 可自行maven打包启动 或 编译工具启动

~~ * 1.启动基础服务：进入docker-compose目录，执行`docker-compose -f docker-compose.yml up` 或单个启动`docker-compose up 服务名`, 服务名如下~~

~~在启动应用之前，需要先启动数据库、缓存、MQ等中间件，可根据自己需要启动的应用选择启动某些基础组件，一般来说启动mysql、redis、kafka、mangodb即可，其它组件若有需要，根据如下命令启动即可。~~

~~该步骤使用了docker快速搭建相应的基础环境，需要你对docker、docker-compose有一定了解和使用经验。也可以不使用docker，自行搭建以下服务即可。~~

|  服务           |   服务名         |  端口     | 备注                                            |
|----------------|-----------------|-----------|-------------------------------------------------|
|  数据库         |   mysql         |  3306     |  目前各应用共用1个实例，各应用可建不同的database     |
|  KV缓存         |   redis         |  6379     |  目前共用，原则上各应用单独实例    |
|  消息中间件      |   rabbitmq      |  5672     |  共用                          |
|  注册与配置中心  |   nacos         |  8848     |  [启动和使用文档](./docs/register.md)             |
|  日志收集中间件  |   zipkin-server |  9411     |  共用                          |
|  搜索引擎中间件  |   elasticsearch |  9200     |  共用    |
|  日志分析工具    |   kibana        |  5601     |  共用    |
|  数据可视化工具  |   grafana       |  3000     |  共用    |

* 2.创建数据库及表

只有部分应用有数据库脚本，若启动的应用有数据库的依赖，请初使化表结构和数据后再启动应用。

docker方式脚本初使化：进入docker-compose目录，执行命令 `docker-compose up mysql-init`

**子项目脚本**

路径一般为：子项目/db

如：`auth/db` 下的脚本，请先执行ddl建立表结构后再执行dml数据初使化

**应用脚本**

路径一般为：子项目/应用名/src/main/db

如：demos/producer/src/main/db 下的脚本

* 3.启动应用

根据自己需要，启动相应服务进行测试，cd 进入相关应用目录，执行命令： `mvn spring-boot:run` 

以下应用都依赖于rabbitmq、nacos，启动服务前请先启动mq和注册中心


# 开发进度

### 基础架构搭建

|  服务     | 使用技术                 |   进度        |    备注   |
|----------|-------------------------|---------------|-----------|
|  注册中心 | Nacos                   |   ✅          |           |
|  配置中心 | Nacos                   |   ✅          |           |
|  消息总线 | SpringCloud kafka       |   🏗          |           |
|  灰度分流 | OpenResty + lua         |   🏗          |           |
|  动态网关 | SpringCloud Gateway     |   🏗          |  多种维度的流量控制（服务、IP、用户等），后端可配置化🏗          |
|  授权认证 | Spring Security OAuth2  |   🏗          |  Jwt模式   |
|  服务容错 | SpringCloud Sentinel    |   🏗          |           |
|  服务调用 | SpringCloud OpenFeign   |   ✅          |           |
|  对象存储 | FastDFS/Minio           |   🏗          |           |
|  任务调度 | Elastic-Job             |   🏗          |           |
|  分库分表 | Mycat                   |   🏗          |           |
|  数据权限 |                         |   🏗          |  使用mybatis对原查询做增强，业务代码不用控制，即可实现。         |

## 更新日志

**2020-10-10：** 


## 联系交流
> qq:742740345

> 邮箱：742740345@qq.com

