package chookin.chubot.web.controller;

import chookin.chubot.server.ChubotServer;
import chookin.chubot.web.model.Agent;
import chookin.chubot.web.model.JobDetail;
import com.jfinal.core.Controller;

import java.util.Collection;

/**
 * Created by zhuyin on 8/19/15.
 */
public class AgentController extends Controller {

    public void index() {
        render("/agents.html");
    }

    public void agents(){
        setAttr("items", ChubotServer.instance().handler().agents());
        renderJson();
    }

    public void agent() throws InterruptedException {
        Collection<JobDetail> jobs = ChubotServer.instance().handler().getJobs(getParaToInt("id"), getPara("status"));
        setAttr("items", jobs);
        renderJson();
    }

    public void history(){
        Collection<Agent> history = Agent.DAO.find("select * from agent");
        setAttr("items", history);
        renderJson();
    }
}
