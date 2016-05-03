# RedisScheduler 普通队列
两个队列：

1. w-task_name 等待被处理的请求
2. d-task_name 已被处理的请求

spider task_name为：

- 如果未指定名称，则为"c-anon"；
- 如果指定，则为：c-name


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
    
# RedisPriorityScheduler 具有优先级的队列
两个队列：

1. pw-task_name-priority 等待被处理的请求
2. pd-task_name 已被处理的请求