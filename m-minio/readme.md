# minio文件系统搭建

#### 前言

整合minio 文件服务器

# 核心配置

> 用到的pom包  | [版本号参考](../pom.xml)

```xml
        <dependency>
                   <groupId>io.minio</groupId>
                   <artifactId>minio</artifactId>
               </dependency>
               <dependency>
                   <groupId>org.springframework.boot</groupId>
                   <artifactId>spring-boot-starter-webflux</artifactId>
               </dependency>
```

# 1 功能介绍

#### 1.1 完成文件上传下载
 
#### 1.2 文件md5校验
	
#### 1.3 断点重连

    
# 2 接口文档

> minio服务/主要接口 

返回示例：
```json
{
"code":20000,
"message":"业务请求成功",
"data":""
}
```

####  2.1 GET 创建存储桶

GET /minio/create/bucket

##### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|bucketName|query|string|true|存储桶名字|

##### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|成功|Inline|

#### 返回数据结构

> 2进制文件

#### 2.2 GET 获取文件（下载方式）

GET /minio/file/get

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|filePath|query|string|true|none|
|bucketName|query|string|true|none|

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|成功|Inline|

### 返回数据结构
> 2进制文件

#### 2.3 DELETE 删除文件

DELETE /minio/file/delete

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|bucketName|query|string|true|存储桶名|
|path|query|string|true|保存路径|

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|成功|Inline|

### 返回数据结构
```json
{
"code":20000,
"message":"业务请求成功",
"data":""
}
```

#### 2.4 DELETE 删除存储桶

DELETE /minio/bucket/delete

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|bucketName|query|string|true|存储桶名|

> 返回示例

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|成功|Inline|

### 返回数据结构

```json
{
"code":20000,
"message":"业务请求成功",
"data":""
}
```

#### 2.5 GET 获取图片（直接获取）

GET /minio/images/get

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|filePath|query|string|true|none|
|bucketName|query|string|true|none|


### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|成功|Inline|

### 返回数据结构

> 2进制文件

#### 2.6 POST 上传文件

POST /minio/stream/uploadObject

> Body 请求参数

```yaml
type: object
properties:
  file:
    type: string
    description: 要上传的文件
    format: binary
  bucketName:
    type: string
    description: 存储桶名称
    example: links
  path:
    type: string
    description: 保存路径
required:
  - file
  - bucketName
  - path

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|Content-Type|header|string|true|none|
|body|body|object|false|none|
|» file|body|string(binary)|true|要上传的文件|
|» bucketName|body|string|true|存储桶名称|
|» path|body|string|true|保存路径|


### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|成功|Inline|

### 返回数据结构
```json
{
    "code": 20000,
    "message": "业务请求成功",
    "data": "存储路径"
}
```