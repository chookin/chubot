package cmri.etl.proxy;

import cmri.utils.configuration.ConfigManager;

/**
 * Created by zhuyin on 9/18/15.
 */
public class ProxyHelper {
    /**
     * Get the initial proxy to connect internet.
     */
    public static Proxy getDefaultProxy() {
        boolean enable = ConfigManager.getBool("proxy.enable", false);
        if (!enable) {
            return new Proxy();
        }
        String host = ConfigManager.get("proxy.host");
        String port = ConfigManager.get("proxy.port");
        String authUser = ConfigManager.get("proxy.user");
        String authPassword = ConfigManager.get("proxy.password");
        return new Proxy()
                .setHost(host)
                .setPort(Integer.parseInt(port))
                .setUser(authUser)
                .setPasswd(authPassword);
    }
}
