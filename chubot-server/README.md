<!-- MarkdownTOC -->

- 编译
- 调试
- 运行
- post example
- java
- javascript
- jquery
- angular
    - directive
- bootstrap
- http
    - HTTP Status Code
- next

<!-- /MarkdownTOC -->

# 编译
cd ~/project/ub-lab/tagbase/src/utils && mvn install -DskipTests \
&& cd ~/project/ub-lab/tagbase/src/etl-commons && mvn install -DskipTests \
&& cd ~/project/chubot/chubot-proto && mvn install -DskipTests \
&& cd ~/project/chubot/chubot-server && mvn clean compile -DskipTests


# 调试
施用IntelliJ IDEA, Maven and the jetty plugin调试web应用程序

配置Maven：
File-->setting-->Maven-->
修改Maven Home Director, User setting file, Locale repository;
选择相应的配置就可以；

配置运行环境：
打开IntelliJ IDEA ，创建Run/Debug的配置，下拉菜单中选择"Edit configurations"，点击左上角的 '+' (plus) 按钮 ，创建一个新的 "Maven" configuration. 具体配置如下：
Working directory：module根目录
Command Line：clean jetty:run -DskipTests
Profile(separated with space): pom.xml

运行/调试：
点击运行或者调试按钮

# 运行
`mvn jetty:run -DskipTests`

{"class":"cmri.tagbase.read.BaiduYueduCollection","collect-categories":"true","scheduler":"cmri.etl.scheduler.RedisPriorityScheduler","proxy.enable":"false","download.concurrent.num":"1","spider.download.sleepMilliseconds":"5000","all":"true","since":"2970-01-02|000000","singleton":"true"}

# post example
curl -d "data=/home/work/project/ub-lab/tagbase/src/tagextract/target/tagextract-1.0-SNAPSHOT.jar" http://192.168.80.131:59000/jobs/addjar
curl -d "data={class:cmri.tagbase.read.BaiduYueduCollection,collect-categories:true,spider.scheduler:cmri.etl.scheduler.RedisPriorityScheduler,proxy.enable:true,download.concurrent.num:10,spider.download.sleepMilliseconds:5000,all:true,since:2970-01-02|000000}" http://192.168.80.131:59000/jobs/commit

# java
getMethodName
to get current method name,  call Thread.currentThread().getStackTrace()[1].getMethodName()
# javascript
for json, must with quotes for key and value except for int or bool; but for java, this is not required.

redirect:
window.location.href = '/jobs.html';
# jquery
http://jquery.com/download/
jQuery 2.x has the same API as jQuery 1.x, but does not support Internet Explorer 6, 7, or 8. 
http://code.jquery.com/jquery-1.11.3.min.js
http://code.jquery.com/jquery-latest.js
# angular
将"use strict"放在脚本文件的第一行，则整个脚本都将以"严格模式"运行。如果这行语句不在第一行，则无效，整个脚本以"正常模式"运行。
The $scope is what lets us bind data to elements in the UI.
AngularJS $http 是一个用于读取web服务器上数据的服务。
http://cdn.bootcss.com/angular.js/1.1.0/angular.min.js
https://code.angularjs.org/1.4.5/angular-cookies.js

start the 2nd ng-app
 angular.bootstrap(angular.element("#pagingApp"),["pagingApp"]);
## directive
You should use dash-separated names inside the html and camelCase for the corresponding name in the directive.
As you can read on the doc: Angular uses name-with-dashes for attribute names and camelCase for the corresponding directive name)
Here: http://docs.angularjs.org/tutorial/step_00

## route
angular的route模块是单独出来的.
因为route是单独的一个模块，所以在咱们实例化app模块的时候，需要在依赖的模块列表中加上route的module名“ngRoute”

## html5
 to your index.html file, under the <head> section a <base> tag, e.g. <base href="/"> . This tells Angular what is the base path of your app so it would know how to change the browser URL correctly. For example, if your Angular app’s root is under http://www.example.com/app , you should probably have a base tag set to <base href="/app/"> .
# bootstrap
http://v3.bootcss.com/
# http
## HTTP Status Code
常见的状态码:

HTTP: Status 200 – 服务器成功返回网页
HTTP: Status 404 – 请求的网页不存在
HTTP: Status 503 – 服务不可用
详解:
HTTP: Status 1xx  (临时响应)
->表示临时响应并需要请求者继续执行操作的状态代码。

详细代码及说明:
HTTP: Status 100 (继续)
-> 请求者应当继续提出请求。 服务器返回此代码表示已收到请求的第一部分，正在等待其余部分。
HTTP: Status 101 (切换协议)
-> 请求者已要求服务器切换协议，服务器已确认并准备切换。
说明:
HTTP Status 2xx  (成功)
->表示成功处理了请求的状态代码;

详细代码及说明:

HTTP Status 200 (成功)
-> 服务器已成功处理了请求。 通常，这表示服务器提供了请求的网页。
HTTP Status 201 (已创建)
-> 请求成功并且服务器创建了新的资源。
HTTP Status 202 (已接受)
-> 服务器已接受请求，但尚未处理。
HTTP Status 203 (非授权信息)
-> 服务器已成功处理了请求，但返回的信息可能来自另一来源。
HTTP Status 204 (无内容)
-> 服务器成功处理了请求，但没有返回任何内容。
HTTP Status 205 (重置内容)
-> 服务器成功处理了请求，但没有返回任何内容。
HTTP Status 206 (部分内容)
-> 服务器成功处理了部分 GET 请求。

说明:

HTTP Status 4xx (请求错误)
->这些状态代码表示请求可能出错，妨碍了服务器的处理。

详细代码说明:
HTTP Status 400 （错误请求） 
->服务器不理解请求的语法。
HTTP Status 401 （未授权） 
->请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。
HTTP Status 403 （禁止）
-> 服务器拒绝请求。
HTTP Status 404 （未找到） 
->服务器找不到请求的网页。
HTTP Status 405 （方法禁用） 
->禁用请求中指定的方法。
HTTP Status 406 （不接受） 
->无法使用请求的内容特性响应请求的网页。
HTTP Status 407 （需要代理授权） 
->此状态代码与 401（未授权）类似，但指定请求者应当授权使用代理。
HTTP Status 408 （请求超时） 
->服务器等候请求时发生超时。
HTTP Status 409 （冲突） 
->服务器在完成请求时发生冲突。 服务器必须在响应中包含有关冲突的信息。
HTTP Status 410 （已删除）
-> 如果请求的资源已永久删除，服务器就会返回此响应。
HTTP Status 411 （需要有效长度） 
->服务器不接受不含有效内容长度标头字段的请求。
HTTP Status 412 （未满足前提条件） 
->服务器未满足请求者在请求中设置的其中一个前提条件。
HTTP Status 413 （请求实体过大） 
->服务器无法处理请求，因为请求实体过大，超出服务器的处理能力。
HTTP Status 414 （请求的 URI 过长） 请求的 URI（通常为网址）过长，服务器无法处理。
HTTP Status 415 （不支持的媒体类型） 
->请求的格式不受请求页面的支持。
HTTP Status 416 （请求范围不符合要求） 
->如果页面无法提供请求的范围，则服务器会返回此状态代码。
HTTP Status 417 （未满足期望值） 
->服务器未满足”期望”请求标头字段的要求。

说明
HTTP Status 5xx （服务器错误）
->这些状态代码表示服务器在尝试处理请求时发生内部错误。 这些错误可能是服务器本身的错误，而不是请求出错。

代码详细及说明:
HTTP Status 500 （服务器内部错误） 
->服务器遇到错误，无法完成请求。
HTTP Status 501 （尚未实施） 
->服务器不具备完成请求的功能。 例如，服务器无法识别请求方法时可能会返回此代码。
HTTP Status 502 （错误网关） 
->服务器作为网关或代理，从上游服务器收到无效响应。
HTTP Status 503 （服务不可用）
-> 服务器目前无法使用（由于超载或停机维护）。 通常，这只是暂时状态。
HTTP Status 504 （网关超时） 
->服务器作为网关或代理，但是没有及时从上游服务器收到请求。
HTTP Status 505 （HTTP 版本不受支持）
-> 服务器不支持请求中所用的 HTTP 协议版本。

# next