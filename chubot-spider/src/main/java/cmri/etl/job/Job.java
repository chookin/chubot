package cmri.etl.job;

import cmri.utils.configuration.ConfigManager;
import cmri.utils.configuration.OptionsPack;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by zhuyin on 8/24/15.
 */
public interface Job extends Runnable {
    Job init(Map<String, String> options);
    Job prepare();
    Job start();
    Job stop();
    JobMetric getMetric();
    OptionsPack getOptions();

    Logger getLogger();
    /**
     * 创建爬虫任务,需要用参数`class`指定具体的爬虫任务类
     */
    static Job createJob(Map<String, String> paras) throws ReflectiveOperationException {
        String jobClass = ConfigManager.get("class", paras, "");
        if(StringUtils.isBlank(jobClass)){
            throw new IllegalArgumentException("fail to create job because of no 'class' option");
        }
        try {
            Job job = (Job) Class.forName(jobClass).newInstance();
            return job.init(paras);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new ReflectiveOperationException("fail to create job instance by paras:" + paras, e);
        }
    }
}
