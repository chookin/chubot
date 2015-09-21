package chookin.chubot.web.jfinal;

import com.jfinal.core.Controller;

/**
 * Created by zhuyin on 9/21/15.
 */
public class BaseController extends Controller{
    public void index() {
        render("/view/index.html");
    }
}
