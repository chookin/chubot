package cmri.utils.configuration;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import cmri.utils.concurrent.ThreadHelper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by zhuyin on 7/6/14.
 */
public class ConfigManager {
    private final static Logger LOG = LoggerFactory.getLogger(ConfigManager.class);
    private static long lastReloadTime = Long.MIN_VALUE;
    private static SortedMap<String, String> paras = new TreeMap<>();
    private static Thread watchDaemon;
    private final static List<String> configFiles = new ArrayList<>();
    /**
     * Interval between two configurations.
     */
    private static int reloadIntervalMillis = 10000;

    static {
        addFile("application.properties");
        addFile("app.properties");
    }

    public static void addFile(String... filenames) {
        if (filenames == null) {
            return;
        }
        for (String filename : filenames) {
            if (configFiles.contains(filename)) {
                continue;
            }
            configFiles.add(filename);
        }
    }

    private static void confirmFiles() {
        List<String> localFiles = new ArrayList<>();
        List<String> classpathFiles = new ArrayList<>();
        for (String filename : configFiles) {
            if (ConfigFileManager.exists(filename)) {
                localFiles.add(filename);
            } else {
                classpathFiles.add(filename);
            }
        }
        LOG.info("local configuration files: " + localFiles);
        LOG.info("classpath configuration files: " + classpathFiles);
    }

    public static boolean exists(String item) {
        return get(item) != null;
    }

    public static synchronized String get(String item) {
        if (item == null) {
            return null;
        }
        String val = System.getProperty(item); // first check jdk system properties.
        if (val != null) {
            return val;
        }
        if (configFiles.isEmpty()) {
            LOG.warn("not add configuration files");
            return null;
        }
        if (paras.isEmpty()) {
            try {
                reConfig();
                startWatchDaemon();
            } catch (Exception e) {
                LOG.error(null, e);
                System.exit(-1);
            }
        }
        val = paras.get(item);
        if (new ParameterParser().parseRegions(val).isEmpty()) {
            return val;
        } else {
            return new ParameterParser().getReal(val);
        }
    }

    public static String get(String item, String defaultValue) {
        String val = get(item);
        return null == val ? defaultValue : val;
    }

    public static String get(String item, Map<String, String> customConfig, String defaultValue) {
        Validate.notNull(customConfig);
        String val = getNotNullVal(item, customConfig);
        return StringUtils.isEmpty(val) ? defaultValue : val;
    }

    /**
     * The {@link boolean} returned represents the value {@code true} if the string argument
     * is not {@code null} and is equal, ignoring case, to the string
     * {@code "true"}. <p>
     */
    public static boolean getBool(String item) {
        String val = checkConfigured(item);
        return Boolean.parseBoolean(val);
    }

    public static boolean getBool(String item, boolean defaultValue) {
        String val = get(item);
        return StringUtils.isEmpty(val) ? defaultValue : Boolean.valueOf(val);
    }

    /**
     * 获取指定参数的配置值,优先从一个指定集合中查找,如果找到直接返回;否则,从配置集中查找,若果找到,返回;否则,返回默认值.
     *
     * @param item         要查找的参数
     * @param customConfig 指定的集合,被优先查找
     * @param defaultValue 默认值
     * @return 参数的配置值
     */
    public static boolean getBool(String item, Map<String, String> customConfig, boolean defaultValue) {
        String val = getNotEmptyVal(item, customConfig);
        return StringUtils.isEmpty(val) ? defaultValue : Boolean.valueOf(val);
    }

    public static int getInt(String item) {
        String val = checkConfigured(item);
        return Integer.parseInt(val);
    }

    public static int getInt(String item, int defaultValue) {
        String val = get(item);
        return StringUtils.isEmpty(val) ? defaultValue : Integer.parseInt(val);
    }

    public static int getInt(String item, Map<String, String> customConfig, int defaultValue) {
        String val = getNotEmptyVal(item, customConfig);
        return StringUtils.isEmpty(val) ? defaultValue : Integer.valueOf(val);
    }

    public static long getLong(String item) {
        String val = checkConfigured(item);
        return Long.parseLong(val);
    }

    public static long getLong(String item, long defaultValue) {
        String val = get(item);
        return StringUtils.isEmpty(val) ? defaultValue : Long.valueOf(val);
    }

    public static long getLong(String item, Map<String, String> customConfig, long defaultValue) {
        String val = getNotEmptyVal(item, customConfig);
        return StringUtils.isEmpty(val) ? defaultValue : Long.valueOf(val);
    }

    public static double getDouble(String item) {
        String val = checkConfigured(item);
        return Double.parseDouble(val);
    }

    public static double getDouble(String item, double defaultValue) {
        String val = get(item);
        return StringUtils.isEmpty(val) ? defaultValue : Double.parseDouble(val);
    }

    /**
     * 优先从<code>customConfig</code>获取属性值,如果获取到的是{@code null},则再尝试从配置文件中获取
     */
    public static String getNotNullVal(String item, Map<String, String> customConfig) {
        Validate.notNull(customConfig);
        String val = customConfig.get(item);
        if (val == null) {
            return get(item);
        }
        return val;
    }

    /**
     * 优先从<code>customConfig</code>获取属性值,如果获取到的是{@code null}或空字符串,则再尝试从配置文件中获取
     */
    public static String getNotEmptyVal(String item, Map<String, String> customConfig) {
        Validate.notNull(customConfig);
        String val = customConfig.get(item);
        if (StringUtils.isEmpty(val)) {
            return get(item);
        }
        return val;
    }

    private static synchronized void startWatchDaemon() {
        if (watchDaemon != null) {
            return;
        }
        watchDaemon = new Thread() {
            @Override
            public void run() {
                while (!currentThread().isInterrupted()) {
                    ThreadHelper.sleep(1000);
                    try {
                        reConfig();
                    } catch (ParserConfigurationException | SAXException | IOException e) {
                        LOG.error("Failed to reload configuration files ", e);
                    }
                }
            }
        };
        watchDaemon.setDaemon(true);
        watchDaemon.start();
    }

    private static String checkConfigured(String property) {
        String val = get(property);
        if (val == null) {
            throw new IllegalArgumentException("property " + property + " is not configured");
        }
        return val;
    }

    private static boolean needReload() {
        return System.currentTimeMillis() >= lastReloadTime + reloadIntervalMillis;
    }

    /**
     * Reload configuration files, and update configuration parameters.
     */
    static synchronized void reConfig() throws ParserConfigurationException, SAXException, IOException {
        if (!needReload()) {
            return;
        }
        SortedMap<String, String> tmpParas = new TreeMap<>();
        lastReloadTime = System.currentTimeMillis();
        for (String filename : configFiles) {
            tmpParas.putAll(loadFile(filename));
        }
        paras = tmpParas;
    }

    private static SortedMap<String, String> loadFile(String filename) throws ParserConfigurationException, SAXException, IOException {
        if (filename.endsWith(".xml")) {
            return loadXmlFile(filename);
        } else if (filename.endsWith(".conf") || filename.endsWith(".ini") || filename.endsWith(".properties")) {
            return loadPropertiesFile(filename);
        }
        return new TreeMap<>();
    }

    /**
     * Streams represent resources which you must always clean up explicitly, by calling the close method.<br>
     * One stream can be chained to another by passing it to the constructor of some second stream. When this second stream is closed, then it automatically closes the original underlying stream as well.<br>
     * With the introduction of Java 7, there are now try-with-resource statements which will automatically close any declared resources when the try block exits.
     */
    private static SortedMap<String, String> loadPropertiesFile(String filename) throws IOException {
        SortedMap<String, String> paras = new TreeMap<>();
        Properties dbProps = new Properties();
        try (InputStream in = ConfigFileManager.getResourceFile(filename)) {
            if (in == null) {
                return paras;
            }
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            dbProps.load(reader);
            dbProps.entrySet().stream()
                    .filter(entry -> entry.getKey() instanceof String && entry.getValue() instanceof String)
                    //.filter(entry -> StringUtils.isEmpty((String) entry.getKey()) || StringUtils.isEmpty((String) entry.getValue()))
                    .forEach(entry -> paras.put((String) entry.getKey(), (String) entry.getValue()));
            return paras;
        }
    }

    private static SortedMap<String, String> loadXmlFile(String filename) throws IOException, SAXException, ParserConfigurationException {
        try (InputStream in = ConfigFileManager.getResourceFile(filename)) {
            if (in == null) {
                return paras;
            }
            return new XmlConfigFile(in).getProperties();
        }
    }
}


