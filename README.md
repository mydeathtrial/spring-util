# spring-util ： Spring扩展工具
[![spring](https://img.shields.io/badge/Spring-LATEST-green)](https://img.shields.io/badge/spring-LATEST-green)
[![maven](https://img.shields.io/badge/build-maven-green)](https://img.shields.io/badge/build-maven-green)
## 它有什么作用

* **Bean工具BeanUtil**
可以于任意位置获取spring应用上下文，并以此获取任意bean

* **国际化信息工具MessageUtil**
依托Agile工具包common-util中的PropertiesUtil扫描任意路径下符合`spring.messages`的国际化配置文件，默认扫描`messages`前缀国际化配置文件
并提供国际化信息翻译功能。

-------
## 快速入门
开始你的第一个项目是非常容易的。

#### 步骤 1: 下载包
您可以从[最新稳定版本]下载包(https://github.com/mydeathtrial/spring-util/releases).
该包已上传至maven中央仓库，可在pom中直接声明引用

以版本spring-util-0.1.jar为例。
#### 步骤 2: 添加maven依赖
```xml
<dependency>
    <groupId>cloud.agileframework</groupId>
    <artifactId>spring-util</artifactId>
    <version>0.1</version>
</dependency>
```
#### 步骤 3: 开箱即用
##### BeanUtil
```
//任意位置取应用上下文
BeanUtil.getApplicationContext()
```
##### MessageUtil
```
//任意位置翻译国际化信息
MessageUtil.message("messageKey", "因为...")
```
国际化文件
```
//中文
messageKey=错啦!{0}
//英文
messageKey=error!{0}
```
结果日志
```
错啦!因为...
```