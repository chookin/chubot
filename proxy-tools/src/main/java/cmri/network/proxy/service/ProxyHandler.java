package cmri.network.proxy.service;

import cmri.etl.common.Request;
import cmri.etl.downloader.StreamDownloader;
import cmri.etl.proxy.ProxiesConfiguration;
import cmri.etl.proxy.Proxy;
import cmri.etl.spider.SpiderAdapter;
import cmri.network.proxy.dao.ProxyDAO;
import cmri.utils.concurrent.FutureService;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.BaseOper;
import cmri.utils.lang.TimeHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by zhuyin on 6/8/15.
 */
public class ProxyHandler extends BaseOper {
    private static final Logger LOG = Logger.getLogger(ProxyHandler.class);

    public static void main(String[] args) {
        new ProxyHandler().setArgs(args).action();
    }

    /**
     * <ul>
     * <li>validate</li>
     * <li>export --out=/tmp/proxies.conf --since=2015-06-09_00:00:00</li>
     * </ul>
     * 参数说明:
     * since date string of format "yyyy-MM-dd_H:m:s"
     */
    @Override
    public boolean action() {
        if (getOptions().process("export")) {
            String out = getOptions().get("out", "/tmp/proxies.conf");
            String sinceTime = getOptions().get("since", "1970-01-01_00:00:00");
            Date since = TimeHelper.parseDate(sinceTime, "yyyy-MM-dd_HH:mm:ss");
            String highAnonymity = getOptions().get("highAnonymity", "true");
            try {
                exportValidProxies(since, Boolean.parseBoolean(highAnonymity), out);
            } catch (IOException e) {
                LOG.error("fail to export proxies to " + out);
            }
            return true;
        } else if (getOptions().process("validate")) {
            String concurrent = getOptions().get("concurrent", "20");
            validateDBProxies(Integer.valueOf(concurrent));
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验代理的可用性,如果发现代理可用,则设置其属性'validateTime'为当前时间,并更新到数据库中
     *
     * @param concurrentNum 进行测试的并发线程数
     */
    private static void validateDBProxies(int concurrentNum) {
        try {
            ProxyDAO dao = ProxyDAO.getInstance();
            Collection<Proxy> proxies = getDBProxies();
            String url = ConfigManager.get("proxy.test.url", "http://www.126.com");
            LOG.info("get " + proxies.size() + " proxies");
            long successCount = new FutureService<>(proxies, items -> () -> {
                long cnt = 0;
                for (Proxy item : items) {
                    if (validateProxy(item, url)) {
                        item.set("validateTime", new Date());
                        dao.update(item);
                        cnt += 1;
                    }
                }
                return cnt;
            }, concurrentNum).action();
            LOG.info(successCount);
        } catch (Exception e) {
            LOG.error(null, e);
        }
    }

    private static Collection<Proxy> getDBProxies() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .put("properties.highAnonymity").is(true);
        ProxyDAO dao = ProxyDAO.getInstance();
        return dao.getCollection().find(queryBuilder.get()).sort(new BasicDBObject("properties.validateTime", -1)).toArray()
                .stream().map(dao::parse).collect(Collectors.toList());
    }


    /**
     * @return true if success to access network by this proxy.
     */
    public static boolean validateProxy(Proxy proxy, String url) {
        // port的取值范围是 0 - 65535(即2的16次方)，0到1024是众所周知的端口（知名端口，常用于系统服务等，例如http服务的端口号是80)。
        if(proxy.getPort() < 0 || proxy.getPort() > 0xFFFF){
            return false;
        }
        if (checkNetwork(proxy, url)) {
            LOG.trace("success to access network on proxy " + proxy);
            return true;
        } else {
            LOG.warn("fail to access network on proxy " + proxy);
            return false;
        }
    }

    /**
     * @return whether or not can connect internet.
     */
    private static boolean checkNetwork(Proxy proxy, String url) {
        try {
            Object resource = StreamDownloader.getInstance().download(new Request().setUrl(url), new SpiderAdapter().setProxy(proxy)).getResource();
            if (resource != null) {
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    /**
     * 从数据库查询最近一段时间验证为可用的代理，并导出到文件中
     *
     * @param since         判定有效性的时间点
     * @param highAnonymity 是否高匿
     */
    private static void exportValidProxies(Date since, boolean highAnonymity, String fileName) throws IOException {
        Collection<Proxy> proxies = queryValidProxies(since, highAnonymity);
        LOG.info("find " + proxies.size() + " available proxies");

        ProxiesConfiguration.getInstance().dump(proxies, fileName);
    }

    /**
     * @return list of queried proxies.
     */
    private static Collection<Proxy> queryValidProxies(Date since, boolean highAnonymity) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .put("properties.highAnonymity").is(highAnonymity)
                .put("properties.validateTime").greaterThanEquals(since);
        return ProxyDAO.getInstance().find(queryBuilder.get());
    }
}
