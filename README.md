# Triclops
以人为本 桥接汽车企业

## 前提条件
- JDK 1.8+
- Gradle 2.5+

## 数据源配置项
下面3个文件是数据源配置文件,请拷贝一份(去掉**-sample**)放置到相同目录中,并修改连接配置以适合你自己的数据库
```
$/src/main/resources/application-sample.yml
$/src/test/resources/application-sample.yml
$/gradle-sample.properties
```
## 数据库初始化命令
```SHELL
    gradle flywayMigrate #迁移数据库并且自动创建数据库的表
    gradle flywayInfo #打印所有迁移的表的详细信息和状态信息
    gradle flywayClean #删除数据库中所有的表
```
可在 http://flywaydb.org/documentation/gradle/ 链接查看更多具体用法

### flyway创建表的命令规则
V<VERSION>__<NAME>.sql，<VERSION>可以写成1 或者 2_1或者3.1
<VERSION>规定写成日期.序号,例如:20150804.1

## 运行
```SHELL
gradle
```
