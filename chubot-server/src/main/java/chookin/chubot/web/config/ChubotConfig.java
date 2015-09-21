package chookin.chubot.web.config;

import chookin.chubot.server.ChubotServer;
import chookin.chubot.web.controller.*;
import chookin.chubot.web.intercepter.LoginInterceptor;
import chookin.chubot.web.jfinal.tablebind.SimpleNameStyles;
import chookin.chubot.web.jfinal.tablebind.TableBindPlugin;
import chookin.chubot.web.model.Agent;
import chookin.chubot.web.model.Job;
import chookin.chubot.web.model.JobDetail;
import chookin.chubot.web.model.User;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.DateHelper;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by zhuyin on 8/19/15.
 */
public class ChubotConfig extends JFinalConfig {
    private static final Logger LOG;

    static {
        try {
            System.setProperty("hostname.time", InetAddress.getLocalHost().getHostName() + "-" + DateHelper.toString(new Date(), "yyyyMMddHHmmss"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOG = Logger.getLogger(ChubotConfig.class);
    }
    /**
     * 在开发模式下，JFinal会对每次请求输出报告，如输出本次请求的Controller，Method以及请求所携带的参数。JFinal支持JSP，Freemarker，Velocity三种常用视图。
     */
    @Override
    public void configConstant(Constants me) {
        me.setDevMode(ConfigManager.getAsBool("devMode", true));
        me.setError404View("/view/common/404.html");
        me.setError500View("/view/common/500.html");
        // constants.setMainRenderFactory(new BeetlRenderFactory());
    }

    /**
     * 配置访问路由，如下代码配置了将“/about”映射到AboutController 这个控制器。通过以下的配置，http://localhost/hello将访问HelloController.index()方法，而http://localhost/hello/methodName/v0-v1 将访问到HelloController.methodName(v0, v1)方法。
     * JFinal 默认使用减号“-”来分割多个参数值（可通过constants.setUrlParaSeparator(String)设置分隔符），在Controller中可以通过getPara(int index)分别取出这些值。
     */
    @Override
    public void configRoute(Routes me) {
        me.add("/", IndexController.class);
        me.add("/admin", AdminController.class);
        me.add("/agents", AgentController.class);
        me.add("/captcha", CaptchaController.class);
        me.add("/help", HelpController.class);
        me.add("/jobs", JobController.class);
        me.add("/user", UserController.class);
    }

    @Override
    public void configPlugin(Plugins me) {
        DruidPlugin dataSourceProvider = createDruid();
        me.add(dataSourceProvider);

        //Model自动绑定表插件
        TableBindPlugin tableBindDefault = new TableBindPlugin(dataSourceProvider, SimpleNameStyles.LOWER);
        tableBindDefault.setContainerFactory(new CaseInsensitiveContainerFactory(true)); //忽略字段大小写
        // tableBindDefault.addExcludePaths("cn.dreampie.function.shop");
        // tableBindDefault.addIncludePaths("chookin.chubot.web.model");
        tableBindDefault.setShowSql(ConfigManager.getAsBool("db.showSql"));
        tableBindDefault.setDialect(new MysqlDialect());
        me.add(tableBindDefault);

        // 配置ActiveRecord插件
        // me.add(createActiveRecordPlugin(dataSourceProvider));

        //ehcache缓存
        me.add(new EhCachePlugin());
    }

    ActiveRecordPlugin createActiveRecordPlugin(IDataSourceProvider dataSourceProvider){
        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(dataSourceProvider);
        activeRecordPlugin
                .setShowSql(ConfigManager.getAsBool("db.showSql"))
                .addMapping("agent", Agent.class)
                .addMapping("job", Job.class) // 映射 job 表到 Job 模型
                .addMapping("jobDetail", JobDetail.class)
                .addMapping("user", User.class)
        ;
        return activeRecordPlugin;
    }
    @Override
    public void configInterceptor(Interceptors me) {
        me.addGlobalActionInterceptor(new LoginInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {}

    @Override
    public void afterJFinalStart() {
        try {
            ChubotServer.instance().start();
        } catch (Exception e) {
            LOG.error(null, e);
            System.exit(-1);
        }

        super.afterJFinalStart();
    }

    @Override
    public void beforeJFinalStop() {
        super.beforeJFinalStop();
        ChubotServer.instance().stop();
    }

    private DruidPlugin createDruid(){
        // 配置Druid 数据库连接池插件
        DruidPlugin dbPlugin = new DruidPlugin(ConfigManager.get("db.url"),
                ConfigManager.get("db.username"),
                ConfigManager.get("db.password"),
                ConfigManager.get("db.driverClass")
        );
        dbPlugin.setInitialSize(ConfigManager.getAsInteger("db.pool.initialSize"))
                .setMinIdle(ConfigManager.getAsInteger("db.pool.minIdle"))
                .setMaxActive(ConfigManager.getAsInteger("db.pool.maxActive"));
        // StatFilter提供JDBC层的统计信息
        dbPlugin.addFilter(new StatFilter());

        // 设置 状态监听与 sql防御
        WallFilter wall = new WallFilter();
        wall.setDbType(ConfigManager.get("db.type"));
        dbPlugin.addFilter(wall);
        return dbPlugin;
    }

    private C3p0Plugin createC3p0(){
        return new C3p0Plugin(ConfigManager.get("db.url"), ConfigManager.get("db.username"),
                ConfigManager.get("db.password"));
    }

    /**
     * 建议使用 JFinal 手册推荐的方式启动项目
     * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
     */
    public static void main(String[] args) {
        JFinal.start("chubot/chubot-server/src/main/webapp", 59000, "/", 5);
    }
}