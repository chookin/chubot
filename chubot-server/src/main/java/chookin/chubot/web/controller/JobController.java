package chookin.chubot.web.controller;

import chookin.chubot.server.ChubotServer;
import chookin.chubot.server.exception.AgentException;
import chookin.chubot.web.model.Job;
import cmri.utils.lang.JsonHelper;
import cmri.utils.lang.MapAdapter;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by zhuyin on 8/19/15.
 */
public class JobController extends Controller {
    private static final Logger LOG = Logger.getLogger(JobController.class);

    public void index() {
        render("/jobs.html");
    }

    @Before(POST.class)
    public void commitJob() {
        try {
            String data = getPara("data");
            if (StringUtils.isNotBlank(data)) {
                Job job = Job.newOne().set("job", getPara("data"));
                String myData = new MapAdapter<String, String>().put(JsonHelper.parseStringMap(data)).put("id", job.get("id")).toJson();
                ChubotServer.instance().handler().commitJob(myData);
                job.save();
            }
        }catch (AgentException ae) {
            setAttr("error", ae.getMessage());
        }
        renderJson();
    }

    public void job(){
        String id=getPara("id");
        renderText("Job " + id);
    }

    @Before(POST.class)
    public void history(){
        pageByOffset();
    }

    /**
     * avoid to use OFFSET or large LIMIT
     */
    private void pageByOffset(){
        String keyword=getPara("key");
        int pageIndex = getParaToInt("pageIndex", 1);
        if(pageIndex < 1) pageIndex = 1;
        int pageSize = getParaToInt("pageSize", 100);
        long total = getParaToInt("total", -1);
        int offset = (pageIndex - 1)*pageSize;
        String limit = " limit " + offset + ", " + pageSize;
        String sql;
        if(StringUtils.isNotBlank(keyword)){
            sql = "select * from job where job like '%" + keyword + "%'"+limit;
        }else{
            sql = "select * from job" + limit;
        }
        List<Job> history = Job.DAO.find(sql);
        setAttr("items", history);
        if(total == -1){
            setAttr("total", historyCount());
        }
        renderJson();
    }

    private long historyCount(){
        return Job.DAO.find("select count(1) as cnt from job").iterator().next().getLong("cnt");
    }
}
