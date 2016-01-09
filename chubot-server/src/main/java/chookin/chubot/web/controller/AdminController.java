package chookin.chubot.web.controller;

import chookin.chubot.server.ChubotServer;
import chookin.chubot.server.exception.AgentException;
import cmri.utils.web.jfinal.BaseController;
import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.POST;

/**
 * Created by zhuyin on 8/26/15.
 */
public class AdminController extends BaseController {
    @Before(POST.class)
    public void addJars(){
        try {
            ChubotServer.instance().handler().addJars(getPara("data"));
        }catch (AgentException ae) {
            setAttr("error", ae.getMessage());
        }
        renderJson();
    }
}
