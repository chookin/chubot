package chookin.chubot.web.controller;

import chookin.chubot.server.ChubotServer;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhuyin on 8/27/15.
 */
public class ControllerHelper {
    private ControllerHelper(){}
    public static boolean generalPost(String data){
        if(StringUtils.isBlank(data)){
            return false;
        }
        ChubotServer.instance().handler().send(Thread.currentThread().getStackTrace()[2].getMethodName(), data);
        return true;
    }
}
