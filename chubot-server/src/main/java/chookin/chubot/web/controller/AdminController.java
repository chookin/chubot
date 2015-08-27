package chookin.chubot.web.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;

/**
 * Created by zhuyin on 8/26/15.
 */
public class AdminController extends Controller {
    public void index() {
        render("/admin.html");
    }

    @Before(POST.class)
    public void addJars(){
        ControllerHelper.generalPost(getPara("data"));
        renderJson();
    }
}
