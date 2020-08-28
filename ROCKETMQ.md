

### rocketMQ在linux环境下安装
1. 首先安装rocketMQ一般都使用源码包进行编译安装，因此需要maven环境。
2. 同时rocketMQ是java开发的组件，因此运行需要jdk/jre支持

<br><br>
#### linux下安装jdk
先去oracle官网下在好jdk的linux二进制包，使用xftp将文件传到linux系统已创建的专门的文件夹下
并进行解压缩：
```shell script
mkdir -p /usr/software/java
cd /usr/software/java
tar -zxvf jdk-8u261-linux-x64.tar.gz
```
设置环境变量：
```shell script
vi /etc/profile
export JAVA_HOME=/usr/software/java/jdk1.8.0_261
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$PATH
```
刷新profile
```shell script
source /etc/profile
```
查看java版本，如果显示版本，证明安装及配置成功
```shell script
[root@localhost java]# java -version
java version "1.8.0_261"
Java(TM) SE Runtime Environment (build 1.8.0_261-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.261-b12, mixed mode)
```
<br><br>
#### linux下安装maven
同样先去官网下好maven的linux的二进制包
然后用xftp将文件传到linux服务器已创建好的文件夹中并解压缩
```shell script
mkdir -p /usr/software/maven
cd /usr/software/maven
tar -zxvf apache-maven-3.6.3-bin.tar.gz
```
设置环境变量：
```shell script
vi /etc/profile
export MAVEN_HOME=/usr/software/maven/apache-maven-3.6.3
export PATH=$MAVEN_HOME/bin:$PATH
```
刷新profile
```bash
source /etc/profile
```
查看maven版本，如果显示正常，说明安装配置成功
```shell script
[root@localhost java]# mvn -v
Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /usr/software/maven/apache-maven-3.6.3
Java version: 1.8.0_261, vendor: Oracle Corporation, runtime: /usr/software/java/jdk1.8.0_261/jre
Default locale: zh_CN, platform encoding: UTF-8
OS name: "linux", version: "3.10.0-1127.el7.x86_64", arch: "amd64", family: "unix"
```
**注意要先安装好jdk再安装maven**
<br><br>
### 安装rocketmq
由于rocketmq的docker容器没有在dockerhub直接发布官方版，dockerhub上存在的镜像的参考文档
又不仔细，一大堆坑，所以不建议使用docker安装rocketmq
<br>这里建议直接安装,并且选择4.5.x版本
首先到官网下载源码包的zip压缩包，如果linux中没有unzip工具那么可以安装
```shell script
yum install unzip
```
使用xftp将文件上传到已创建好的文件夹中并解压
```shell script
mkdir -p /usr/software/rocketmq
cd /usr/software/rocketmq
unzip unzip rocketmq-all-4.5.1-source-release.zip

#通过mvn进行源码编译，下面的命令直接复制即可
cd rocketmq-all-4.5.1
mvn -Prelease-all -DskipTests clean install -U
```
由于初始配置中，启动时jvm的内存设置到了4g及8g，因此需要先进入bin目录去修改.sh的对jvm配置的文件
```shell script
cd distribution/target/rocketmq-4.5.1/rocketmq-4.5.1/bin
#修改runserver.sh
vi runserver.sh
#将JAVA_OPT修改为如下配置
JAVA_OPT="${JAVA_OPT} -server -Xms128m -Xmx128m -Xmn128m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
#修改runbroker.sh
vi runbroker.sh
#将JAVA_OPT修改为如下配置
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m"
```
由于需要暴露公网ip让外界进行访问，因此还需要进入conf目录修改broker.conf
```shell script
cd /conf
vi broker.conf
#加上以下内容
brokerIP1 = [你的公网IP]
``` 
如果服务器的公网ip访问即inet没有开启，需要先去开启
```shell script
vi /etc/sysconfig/network-scripts/ifcfg-ens33
#将最后一行的ONBOOT=no 改成yes
ONBOOT=yes
#重启网络服务
service network start
```
回到bin目录的父级目录，先启动name-server
```shell script
cd ..
nohup sh bin/mqnamesrv &
#输入以下命令查看日志，是否启动成功
tail -f ~/logs/rocketmqlogs/namesrv.log
```
name-server启动成功后，才可以启动broker
```shell script
nohup sh bin/mqbroker -n [你的公网ip]:9876 &
#输入以下命令查看日志，是否启动成功
tail -f ~/logs/rocketmqlogs/broker.log
```
<br><br>
#### 安装管理控制台
到下面的网址下载该控制台的源码压缩包
https://github.com/apache/rocketmq-externals/tree/release-rocketmq-console-1.0.0
<br>本地解压缩后，先到resource文件下修改application.properties文件配置
将namesrv的地址修改成刚才配置的namesrv，即[你的公网ip]:9876；
并且进行maven打包(跳过测试)
```shell script
mvn clean package -DskipTests
```
进入target文件夹下，将可运行的jar文件通过xftp上传到已创建好的文件夹中
```shell script
mkdir -p /usr/software/rocketmq/rmqconsole
cd /usr/software/rocketmq/rmqconsole
#记得后台运行，运行成功后浏览器输入公网ip:8080进行访问
java -jar xxxxx.jar &
```
前台切换后台运行，先按Ctrl+Z，然后输入bg%[number]
将后台切换到前台，先输入jobs -l查看具体的任务号码（第一列[]内的数字）
然后输入fg%[number]