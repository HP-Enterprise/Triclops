# Triclops
以人为本 桥接汽车企业

[![Build Status](https://travis-ci.org/HP-Enterprise/Triclops.svg?branch=dev)](https://travis-ci.org/HP-Enterprise/Triclops)

## 前提条件
- JDK 1.8+
- Gradle 2.5+

## 配置文件
本项目使用的配置文件位于
- [$/src/main/resources/application.yml](https://github.com/HP-Enterprise/Triclops/blob/dev/src/main/resources/application.yml)
- [$/src/test/resources/application.yml](https://github.com/HP-Enterprise/Triclops/blob/dev/src/test/resources/application.yml)
- 默认激活dev配置,因此,可以在`$/src/*/resources/`下创建一个名为`application-dev.yml`的配置文件,按自己的需要重载配置项
- 也可以通过定义一个名为spring.profiles.active的系统属性来指定激活的配置,例如:
```SHELL
gradle -Dspring.profiles.active=product run
```
- 那么直接运行时 $/src/main/resources/application-product.yml 将被激活.
- 单元测试时 $/src/test/resources/application-product.yml 将被激活.
- 没有在`application-product.yml`里定义的配置,会继承`application.yml`里的定义.

## 数据库初始化命令
数据库初始化需要在`$/gradle.properties`中配置数据库连接,请参考示例文件`$/gradle-sample.properties`
```SHELL
    gradle flywayMigrate #迁移数据库并且自动创建数据库的表
    gradle flywayInfo #打印所有迁移的表的详细信息和状态信息
    gradle flywayClean #删除数据库中所有的表
```
可在 http://flywaydb.org/documentation/gradle/ 链接查看更多具体用法

### flyway创建SQL脚本的文件命名规则
```
V<VERSION>__<NAME>.sql，<VERSION>可以写成1 或者 2_1或者3.1
<VERSION>规定写成日期.序号,例如:20150804.1
```

## 运行
```SHELL
gradle run
```
