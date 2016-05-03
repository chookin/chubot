package cmri.etl.proxy;

import cmri.etl.common.Request;
import cmri.etl.common.Stat;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.WeightCircleLink;
import cmri.utils.web.UrlHelper;
import org.apache.log4j.Logger;
import org.jsoup.helper.Validate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 7/3/15.
 */
public class ProxySchedulerImpl implements ProxyScheduler {
    private static final Logger LOG = Logger.getLogger(ProxySchedulerImpl.class);
    private final List<Proxy> proxies = new ArrayList<>();
    private final ReadWriteLock proxiesLock = new ReentrantReadWriteLock();

    /** map of {proxy:{domain, proxyStat}} */
    private final Map<Proxy, Map<String, Stat>> proxyStatMap = new HashMap<>();

    /**
     * 为每个抓取频次需要限制的网站维护一个首尾相连的资源链表
     *
     * map of {网站域名:代理链表}
     */
    private final Map<String, WeightCircleLink<Proxy>> domainProxies = new HashMap<>();
    private final ProxyBlacklist blacklist = new ProxyBlacklist();

    static final ProxySchedulerImpl instance = new ProxySchedulerImpl();
    public static ProxySchedulerImpl getInstance(){
        return instance;
    }
    protected ProxySchedulerImpl(){
        init();
    }

    private void init() {
        Set<Proxy> proxies = loadConfiguredProxies();
        Proxy defaultProxy = ProxyHelper.getDefaultProxy();
        proxiesLock.writeLock().lock();
        try {
            this.proxies.clear();
            this.proxies.addAll(proxies);
            this.proxies.add(defaultProxy);
            Collections.sort(this.proxies, (o1, o2) -> o2.get("weight", 1).compareTo(o1.get("weight", 1)));
        }finally {
            proxiesLock.writeLock().unlock();
        }
    }

    @Override
    public ProxySchedulerImpl onSuccess(Request request){
        Proxy proxy = (Proxy) request.getExtra("proxy");
        if(proxy != null) {
            getStat(proxy, getDomain(request)) // 获取该代理关于所请求网站的访问统计器
                    .add(true); // 记录该次访问成功
        }
        return this;
    }

    @Override
    public ProxySchedulerImpl onError(Request request){
        Proxy proxy = (Proxy) request.getExtra("proxy");
        if(proxy != null) {
            String domain = getDomain(request);
            Stat stat = getStat(proxy, domain);
            stat.add(false); // 记录该次访问失败
            if(!stat.isOK()){ // 判定该代理能否继续用于该网站的请求
                blacklist.mark(domain, proxy);
            }
        }
        return this;
    }

    @Override
    public Proxy getProxy(Request request){
        Validate.notNull(request, "request");
        Proxy proxy = getAvailableProxy(getDomain(request));
        request.putExtra("proxy", proxy);
        return proxy;
    }

    /**
     * Load proxies information from proxies configuration file.
     *
     * @return Proxies information.
     */
    Set<Proxy> loadConfiguredProxies() {
        try {
            return ProxiesConfiguration.getInstance().loadConfiguredProxies();
        } catch (IOException e) {
            LOG.error("fail to load proxies configuration file ");
            return new HashSet<>();
        }
    }


    /**
     * 获取指定代理关于指定网站的状态统计
     */
    private Stat getStat(Proxy proxy, String domain){
        Map<String, Stat> domains = proxyStatMap.get(proxy);
        if(domains == null){
            domains = new HashMap<>();
            proxyStatMap.put(proxy, domains);
        }
        Stat stat = domains.get(domain);
        if(stat == null){
            stat = new Stat(ConfigManager.getInt("spider.proxies.stat.queueCapacity", 3),
                    ConfigManager.getInt("spider.proxies.stat.failCountThreshold", 2),
                    ConfigManager.getInt("spider.proxies.stat.expire", 180000));
            domains.put(domain, stat);
        }
        return stat;
    }

    /**
     * @param domain 网站域名
     *
     * @return if all the proxies for {@code domain} are in blacklist, then return default proxy.
     */
    private Proxy getAvailableProxy(String domain){
        Proxy proxy = getDomainProxies(domain)
                .switchNext() // 切换到下个可用代理,目的是为了降低代理在一定时段内的使用频次,从而降低被网站拒绝访问的几率
                .current();
        if(proxy == null){
            proxy = ProxyHelper.getDefaultProxy();
        }
        return proxy;
    }

    private WeightCircleLink<Proxy> getDomainProxies(String domain){
        WeightCircleLink<Proxy> myProxies = domainProxies.get(domain);
        if(myProxies == null){ // if not exists, create and add
            // 初始时,每个网站的代理链表都使用代理池中的所有代理
            myProxies = new WeightCircleLink<>();
            for(Proxy proxy: this.proxies){
                myProxies.add(proxy, proxy.get("weight", 1));
            }
            domainProxies.put(domain, myProxies);
        }else{
            Collection<Proxy> removed = new ArrayList<>();
            Collection<Proxy> blacks = blacklist.get(domain, removed);
            if(blacks != null)
                myProxies.remove(blacks); // 移除不可使用的
            // add proxies that have been removed from blacklist
            for(Proxy proxy: removed){ // 添加恢复使用的
                myProxies.add(proxy, proxy.get("weight", 1));
            }
        }
        return myProxies;
    }

    /**
     * 获取所请求网站的完整域名,包括子域名
     */
    private String getDomain(Request request){
        return UrlHelper.getBaseDomain(request.getUrl());
    }
}
