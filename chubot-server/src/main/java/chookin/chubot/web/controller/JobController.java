package chookin.chubot.web.controller;

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
    public void commitJob(){
        String data = getPara("data");
        if(StringUtils.isNotBlank(data)){
            Job job = Job.newOne().set("job", getPara("data"));
            String myData = new MapAdapter<String,String>().put(JsonHelper.parseStringMap(data)).put("id", job.get("id")).toJson();
            ControllerHelper.generalPost(myData);
            job.save();
        }
        renderJson();
    }

    public void job(){
        String id=getPara("id");
        renderText("Job "+id);
    }

    public void history(){
        String keyword=getPara("key");
        String sql;
        if(StringUtils.isNotBlank(keyword)){
            sql = "select * from job where job like '%" + keyword + "%' order by time asc";
        }else{
            sql = "select * from job" + " order by time asc";
        }
        List<Job> history = Job.DAO.find(sql);
        setAttr("items", history);
        renderJson();
    }
}
