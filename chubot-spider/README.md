# jmxtools
在maven官网中无法下载，因此从他处下载（现已拷贝到lib文件夹下），并手动加入到maven本地仓库。
```shell
mvn install:install-file -Dfile=lib/jmxtools-1.2.1.jar -DgroupId=com.sun.jdmk -DartifactId=jmxtools -Dversion=1.2.1 -Dpackaging=jar -DgeneratePom=true
```
# Redis
## 操作
### 启动
cd ~/local/redis/bin && ./redis-server  ~/local/redis/redis.conf
### 查看所有keys
redis-cli --raw KEYS "*"
如果不加--raw，汉字会以16进制串显示，例如:pd-c-music.migu/\xe6\xb3\xb0\xe8\xaf\xad
### 查看名字以”pw”开头的keys
redis-cli --raw KEYS "pw*"
### 删除所有以“pd-c-music.migu”开头的keys，需要-d选项指定分隔符为换行符（xargs默认是以空格为分隔符）
redis-cli KEYS "pd-c-music.migu*" | xargs -d \\n redis-cli DEL
redis-cli KEYS "pd-c-youku*" | xargs -d \\n redis-cli DEL
redis-cli KEYS "youku*" | xargs -d \\n redis-cli DEL
### 键存储
#### 普通队列
两个队列：
1)	w-task_name 等待被处理的请求
2)	d-task_name 已被处理的请求
task_name为：
如果未指定名称，则为"anon"；
如果指定，则为：c-[prefix/]site/categoryName

    public Spider(String name, Scheduler scheduler) {
        this.name = generateTaskName(name);
        if(scheduler == null){
            this.scheduler = new PriorityScheduler();
        }else{
            this.scheduler = scheduler;
        }
    }
    
    private String generateTaskName(String name){
        String prefix = "c-"; // "collect-"
        if (name == null || name.isEmpty()) {
            return  "anon"; // anonymous
        } else if (name.startsWith(prefix)) {
            return name;
        } else {
            return prefix + name;
        }
    }
#### 具有优先级的队列
两个队列：
*	pw-task_name-priority 等待被处理的请求
*	pd-task_name 已被处理的请求

# question


