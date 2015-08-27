package chookin.chubot.web.controller;

import com.jfinal.core.Controller;

/**
 * Created by zhuyin on 8/19/15.
 */
public class HelpController extends Controller {
    public void index() {
        renderText("Hello World to Chubot.");
    }
}
