package cmri.etl.common;

import cmri.utils.concurrent.ThreadHelper;
import cmri.utils.configuration.ConfigFileManager;
import cmri.utils.web.UrlHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by zhuyin on 3/6/15.
 */
public class ValidateHelper {
    private static final Logger LOG = Logger.getLogger(ValidateHelper.class);
    private static final String configfileName = "validation-mark.conf";
    private static final ValidateHelper instance = new ValidateHelper();
    private static Thread watchDaemon;
    private final Map<String, Set<String>> validations = new ConcurrentHashMap<>();
    private long lastReloadTime = Long.MIN_VALUE;

    public static boolean isValidationPage(String url, String html) {
        String domain = UrlHelper.getBaseDomain(url);
        for (String mark : instance.getValidationMarks(domain)) {
            if(mark.equals("*")){
                return true;
            }
            if (html.contains(mark)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized void startWatchDaemon() {
        if (watchDaemon != null) {
            return;
        }
        watchDaemon = new Thread() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        ThreadHelper.sleep(1000);
                        instance.reload();
                    }
                } catch (Throwable e) {
                    LOG.fatal(null, e);
                }
            }
        };
        watchDaemon.setDaemon(true);
        watchDaemon.start();
    }

    private Set<String> getValidationMarks(String domain) {
        if (validations.isEmpty()) {
            try {
                ConfigFileManager.dumpIfNotExists(configfileName);
            } catch (IOException e) {
                LOG.error("Failed to dump " + configfileName, e);
                System.exit(-1);
                return validations.get(domain);
            }
            reload();
            startWatchDaemon();
        }
        Set<String> marks = validations.get(domain);
        if (marks == null) {
            return new HashSet<>();
        } else {
            return marks;
        }
    }

    private boolean needReload() {
        long now = System.currentTimeMillis();
        // time interval between two configurations, ms
        long configMinInter = 5 * 1000;
        if (now < lastReloadTime + configMinInter) {
            return false;
        }
        return true;
    }

    private void reload() {
        if (!needReload()) {
            return;
        }
        lastReloadTime = System.currentTimeMillis();

        Map<String, Set<String>> tmpValidations = getValidationMarksConfiguration();
        validations.clear();
        validations.putAll(tmpValidations);
    }

    private Map<String, Set<String>> getValidationMarksConfiguration() {
        Map<String, Set<String>> tmpValidations = new TreeMap<>();

        List<String> lines;
        try {
            lines = ConfigFileManager.readLines(configfileName);
        } catch (IOException e) {
            LOG.error("Failed to load " + configfileName);
            return validations;
        }

        for (String line : lines) {
            String myLine = line.trim();
            if (myLine.isEmpty() || myLine.startsWith("#")) {
                continue;
            }
            int firstDotIndex = myLine.indexOf(",");
            if (firstDotIndex == -1) {
                continue;
            }
            String site = myLine.substring(0, firstDotIndex).trim();
            String mark = myLine.substring(firstDotIndex + 1).trim();

            Set<String> marks = tmpValidations.get(site);
            if (marks == null) {
                marks = new HashSet<>();
                tmpValidations.put(site, marks);
            }
            marks.add(mark);
        }
        return tmpValidations;
    }

}
