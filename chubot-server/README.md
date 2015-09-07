# 编译
cd ~/project/ub-lab/tagbase/src/utils && mvn install -DskipTests \
&& cd ~/project/ub-lab/tagbase/src/etl-commons && mvn install -DskipTests \
&& cd ~/project/chubot/chubot-proto && mvn install -DskipTests \
&& cd ~/project/chubot/chubot-server && mvn clean compile -DskipTests


# 调试
施用IntelliJ IDEA, Maven and the jetty plugin调试web应用程序

配置Maven：
File-->setting-->Maven-->
修改Maven Home Director, User setting file, Locale repostiory;
选择相应的配置就可以；

配置运行环境：
打开IntelliJ IDEA ，创建Run/Debug的配置，下拉菜单中选择"Edit configurations"，点击左上角的 '+' (plus) 按钮 ，创建一个新的 "Maven" configuration. 具体配置如下：
Working directory：module根目录
Command Line：clean jetty:run -DskipTests
Profile(separated with space): pom.xml

运行/调试：
点击运行或者调试按钮

# 运行
mvn jetty:run -DskipTests

# post example
curl -d "data=/home/work/project/ub-lab/tagbase/src/tagextract/target/tagextract-1.0-SNAPSHOT.jar" http://192.168.80.131:59000/api/spider/job/addjar
curl -d "data={class:cmri.tagbase.read.BaiduYueduCollection,collect-categories:true,scheduler:cmri.etl.scheduler.RedisPriorityScheduler,proxy.enable:true,download.concurrent.num:10,download.sleepMilliseconds:5000,all:true,since:2970-01-02|000000}" http://192.168.80.131:59000/api/spider/job/commit

# java
getMethodName
to get current method name,  call Thread.currentThread().getStackTrace()[1].getMethodName()
# javascript
for json, must with quotes for key and value except for int or bool; but for java, this is not required.
# angular
将"use strict"放在脚本文件的第一行，则整个脚本都将以"严格模式"运行。如果这行语句不在第一行，则无效，整个脚本以"正常模式"运行。
The $scope is what lets us bind data to elements in the UI.
AngularJS $http 是一个用于读取web服务器上数据的服务。

## directive
You should use dash-separated names inside the html and camelCase for the corresponding name in the directive.
As you can read on the doc: Angular uses name-with-dashes for attribute names and camelCase for the corresponding directive name)
Here: http://docs.angularjs.org/tutorial/step_00
# bootstrap
http://v3.bootcss.com/
# next