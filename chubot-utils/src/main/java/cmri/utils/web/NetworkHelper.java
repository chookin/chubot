package cmri.utils.web;

import cmri.utils.configuration.ConfigManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.net.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Created by work on 12/12/14.
 */
public class NetworkHelper {
    private static final Logger LOG = Logger.getLogger(NetworkHelper.class);
    /**
     * Set proxy to access network. If property "proxy.enable" is true, then set proxy that configured by "proxy.host", "proxy.port". If failed to access network by default proxy, then use no proxy.
     */
    public static void setDefaultProxy() {
        boolean enable = ConfigManager.getBool("proxy.enable", false);
        if (!enable) {
            return;
        }
        String host = ConfigManager.get("proxy.host");
        int port = ConfigManager.getInt("proxy.port");
        String user = ConfigManager.get("proxy.user");
        String password = ConfigManager.get("proxy.password");
        setProxy(host, port, user, password);
    }

    /**
     * Use command is:
     * java -Dhttp.proxyHost=proxy.cmcc  -Dhttp.proxyPort=8080 -jar my.jar
     * Beware of setting System global proxyHost and proxyPort throuch System.setProperty when running your application in an Application Server. The proxy setting will influence all other applications running in the same Java Virtual Machine.
     */
    public static void setProxy(String host, int port, String user, String password) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", String.valueOf(port));
        if(StringUtils.isNotBlank(user)){
            System.setProperty("http.proxyUser", user);
            System.setProperty("http.proxyPassword", password);
        }
        LOG.info("Set proxy: " + host+":"+port);
    }

    /**
     * @return host's hostname
     */
    public static String getHostname(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.error("fail to get hostname", e);
            return "unknown";
        }
    }

    /**
     * @return host's ip collection.
     */
    public static Collection<String> getIPs(){
        Collection<String> ips =new HashSet<>();
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    ips.add(i.getHostAddress());
                }
            }
        } catch (SocketException e1) {
            LOG.error("fail to get host ip", e1);
        }
        return ips;
    }
}