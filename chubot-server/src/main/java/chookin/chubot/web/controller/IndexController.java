package chookin.chubot.web.controller;

import com.jfinal.core.Controller;

/**
 * Created by zhuyin on 8/25/15.
 */
public class IndexController extends Controller{
    public void index() {
		render("/index.html");
    }
}
