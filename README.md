#### 备注

基于NBIoT的通信有Bug，项目基于ESP的WiFi模块实现通信

yarn install安装前端依赖

yarn run serve运行前端程序

数据库丢失...字段值可以从后端程序中分析



#### 服务器搭建

##### 操作系统

操作系统：CentOS 7

云服务器公网IP：123.249.47.82（2024已弃用）

使用云服务器进行服务器端的搭建，使用操作系统为CentOS7

##### 数据库

数据库MySQL版本： mysql  Ver 8.0.35 for Linux on aarch64

数据库服务启用状态查询：sudo systemctl status mysqld

启用服务端口：3306

用户&密码：root  Mqtt@1912

数据表：记录温湿度采集条目

##### MQTT服务器

MQTT消息服务器EMQX版本：EMQX 5.1.6

EMQX服务状态查询：sudo systemctl status emqx

MQTT TCP协议端口：1883

MQTT 后台服务端口：18083

MQTT 后台账户：admin  Mqtt@1912

订阅主题：eps8266-1912