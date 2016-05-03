package cmri.etl.proxy;

import cmri.etl.common.MapItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhuyin on 2/12/15.

 <ul>属性说明:
 <li>"country", 国家</li>
 <li>"location", 位置</li>
 <li>"highAnonymity", 是否高匿</li>
 <li>"type", 类型，取值为：HTTP or HTTPS.</li>
 <li>"accessTime", Time usage to access, in seconds.</li>
 <li>"connectTime", Time usage to establish connection, in seconds</li>
 <li>"validateTime", Time of validate this proxy usability.</li>
 </ul>
 */
public class Proxy implements MapItem {
    private String host = "";
    private int port;
    private String user = "";
    private String passwd = "";
    private final Map<String, Object> properties = new TreeMap<>();

    public Proxy set(String name, Object value) {
        if(value == null){
            return this;
        }
        if(value instanceof String && ((String) value).isEmpty()){
            return this;
        }
        this.properties.put(name, value);
        return this;
    }

    public Object get(String propertyName) {
        return properties.get(propertyName);
    }

    public <T> T get(String propertyName, T defaultVal){
        Object val = properties.get(propertyName);
        if(val == null){
            return defaultVal;
        }
        return (T) val;
    }

    public Map<String, Object> getProperties(){
        return properties;
    }

    public String getHost() {
        return host;
    }

    public Proxy setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Proxy setPort(int port) {
        this.port = port;
        return this;
    }

    public String getUser() {
        return user;
    }

    public Proxy setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPasswd() {
        return passwd;
    }

    public Proxy setPasswd(String passwd) {
        this.passwd = passwd;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proxy proxy = (Proxy) o;

        if (port != proxy.port) return false;
        if (host != null ? !host.equals(proxy.host) : proxy.host != null) return false;
        if (passwd != null ? !passwd.equals(proxy.passwd) : proxy.passwd != null) return false;
        if (user != null ? !user.equals(proxy.user) : proxy.user != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (passwd != null ? passwd.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", passwd='" + passwd + '\'' +
                ", properties=" + properties +
                '}';
    }

    public java.net.Proxy getJavaProxy(){
        return new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    public HttpHost getHttpHost(){
        if(StringUtils.isBlank(host)){
            return null;
        }
        return new HttpHost(host, port);
    }
    @Override
    public Map<String, Object> toStringMap() {
        Map<String, Object> item = new HashMap<>();
        item.put("collection", "proxy");
        item.put("_id", getId());
        item.put("host", host);
        item.put("port", port);
        item.put("user", user);
        item.put("passwd", passwd);
        if(!properties.isEmpty())
            item.put("properties", properties);
        return item;
    }

    @Override
    public String getId() {
        return String.format("%s-%d", host, port);
    }
}
