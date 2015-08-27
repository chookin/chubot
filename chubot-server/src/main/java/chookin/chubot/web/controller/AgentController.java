package chookin.chubot.web.controller;

import chookin.chubot.server.ChubotServer;
import chookin.chubot.web.model.Agent;
import com.jfinal.core.Controller;

import java.util.List;

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

    public void agent(){
        long id = getParaToLong("id");
        renderHtml("agent");
    }

    public void history(){
        List<Agent> history = Agent.DAO.find("select * from agent");
        setAttr("items", history);
        renderJson();
    }
}
