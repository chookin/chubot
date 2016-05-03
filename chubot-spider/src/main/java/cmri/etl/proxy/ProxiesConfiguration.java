package cmri.etl.proxy;

import cmri.utils.configuration.ConfigFileManager;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.io.FileHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chookin on 16/4/29.
 */
public class ProxiesConfiguration {
    private ProxiesConfiguration(){}

    public static ProxiesConfiguration getInstance(){
        return new ProxiesConfiguration();
    }

    public void dump(Collection<Proxy> proxies, String fileName) throws IOException {
        StringBuilder strb = new StringBuilder()
                .append("# Proxies list, and one line one proxy.").append("\n")
                .append("# Each line is in format: host, port, weight, user, password, desc").append("\n")
                .append("# Skip the line start with \"#\".").append("\n")
                .append("\n");
        String sep = ",";
        for (Proxy proxy : proxies) {
            strb.append(proxy.getHost()).append(sep)
                    .append(proxy.getPort())
                    .append(proxy.get("weight", 1)).append(sep)
            ;
            if (StringUtils.isNotEmpty(proxy.getUser())) {
                strb.append(sep)
                        .append(proxy.getUser()).append(sep)
                        .append(proxy.getPasswd()).append(sep)
                        .append(proxy.get("desc") == null ? "" : proxy.get("desc"))
                ;
            }

            strb.append("\n");
        }
        FileHelper.save(strb.toString(), fileName);
    }

    /**
     * Load proxies information from configuration file.
     *
     * @return Proxies information.
     */
    public Set<Proxy> loadConfiguredProxies() throws IOException {
        Set<Proxy> myProxies = new HashSet<>();
        boolean enablesProxies = ConfigManager.getBool("spider.proxies.enable", false);
        if(!enablesProxies)
            return myProxies;
        String fileName = ConfigManager.get("spider.proxies.file", "proxies.conf");
        List<String> lines = ConfigFileManager.readLines(fileName);
        for (String item : lines) {
            Proxy proxy = parse(item);
            if(proxy != null)
                myProxies.add(proxy);
        }
        return myProxies;
    }

    /**
     * 解析代理资源配置文件中的每一行
     */
    private Proxy parse(String line){
        if (StringUtils.isEmpty(line)) {
            return null;
        }
        if (line.trim().startsWith("#")) {
            return null;
        }

        String[] arr = line.split(",");
        if(arr.length < 2){
            return null;
        }
        Proxy proxy = new Proxy();
        proxy.setHost(arr[0].trim())
                .setPort(Integer.parseInt(arr[1].trim()));
        if(arr.length == 3){
            proxy.set("weight", Integer.valueOf(arr[2].trim()));
        }
        if(arr.length == 5){
            proxy.setUser(arr[3].trim())
                    .setPasswd(arr[4].trim());
        }
        if(arr.length == 6){
            proxy.set("desc", arr[5].trim());
        }
        return proxy;
    }
}
