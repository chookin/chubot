package cmri.etl.proxy;

import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.Pair;
import org.jsoup.helper.Validate;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代理黑名单
 *
 * Created by zhuyin on 7/7/15.
 */
class ProxyBlacklist implements Serializable{
    /**
     * map of {网站域名: { 代理被标记为不可用的时间戳: 代理信息} }
     */
    private final Map<String, Queue<Pair<Long, Proxy>>> domainProxies = new HashMap<>();

    /**
     * 标记代理不可继续用于该网站的抓取
     */
    public ProxyBlacklist mark(String domain, Proxy proxy) {
        Queue<Pair<Long, Proxy>> queue = domainProxies.get(domain);
        if (queue == null) {
            queue = new LinkedList<>();
            domainProxies.put(domain, queue);
        }
        queue.add(new Pair<>(System.currentTimeMillis(), proxy));
        return this;
    }

    /**
     * 用于返回指定网站不可使用的代理,以及那些已恢复可用的代理
     *
     * @param domain 网站域名
     * @param removed 用于返回已恢复可用的代理
     * @return 不可用的代理
     */
    public Collection<Proxy> get(String domain, Collection<Proxy> removed) {
        Validate.notNull(removed, "removed");
        removed.clear();
        Queue<Pair<Long, Proxy>> queue = domainProxies.get(domain);
        if (queue == null) {
            return null;
        }
        removed.addAll(removeExpires(queue));
        if (queue.isEmpty()) {
            domainProxies.remove(domain);
            return null;
        }
        return queue.stream().map(Pair::getValue).collect(Collectors.toList());
    }

    /**
     * 移除队列中过期的代理,这些代理将恢复可用性. 背景:当经过一段时间后预估该代理恢复可用时，再次把该网站的代理链表中该代理的所有引用标记为可用.
     * @param queue 代理队列
     * @return 被移除的过期的代理
     */
    List<Proxy> removeExpires(Queue<Pair<Long, Proxy>> queue) {
        List<Proxy> removed = new ArrayList<>();
        while (!queue.isEmpty()) {
            Pair<Long, Proxy> front = queue.peek();
            if (System.currentTimeMillis() - front.getKey() > ConfigManager.getLong("spider.proxies.blackProxy.expire", 7200000)) {
                removed.add(queue.poll().getValue());
            } else {
                break;
            }
        }
        return removed;
    }
}
