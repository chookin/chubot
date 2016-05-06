package cmri.utils.lang;

import cmri.utils.configuration.ConfigManager;
import cmri.utils.configuration.OptionsPack;
import cmri.utils.web.NetworkHelper;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by zhuyin on 3/24/15.
 */
public abstract class BaseOper {
    private static Logger LOG;
    private final OptionsPack options = new OptionsPack();

    static {
        // configure log4j to log to custom file at runtime. In the java program directly by setting a system property (BEFORE you make any calls to log4j).
        try {
            String actionName = System.getProperty("action");
            String name = InetAddress.getLocalHost().getHostName() + "-" + TimeHelper.toString(new Date(), "yyyyMMdd.HH");
            if(actionName ==null) {
                System.setProperty("hostname.time", name);
            }else{
                System.setProperty("hostname.time", actionName + "-" + name);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOG = Logger.getLogger(BaseOper.class);
        NetworkHelper.setDefaultProxy();
    }

    public Logger getLogger(){
        return LOG;
    }

    /**
     * 当args为空时，采用配置参数"cli.paras"所指定的
     */
    static String[] getArgs(String[] args){
        if(args == null || args.length == 0){
            return ConfigManager.get("cli.paras","").split(" ");
        }else{
            return args;
        }
    }

    public BaseOper setArgs(String[] args){
        String[] myArgs = getArgs(args);
        options.put(myArgs);
        LOG.info("args: " + Arrays.toString(myArgs));
        return this;
    }

    public BaseOper setArgs(Map<String, String> options){
        this.options.put(options);
        return this;
    }
    public OptionsPack getOptions(){
        return options;
    }

    /**
     * @return true if execute.
     */
    public abstract boolean action();
}