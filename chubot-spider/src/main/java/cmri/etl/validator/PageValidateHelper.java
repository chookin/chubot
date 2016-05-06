package cmri.etl.validator;

import cmri.utils.concurrent.ThreadHelper;
import cmri.utils.configuration.ConfigFileManager;
import cmri.utils.web.UrlHelper;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuyin on 3/6/15.
 */
public class PageValidateHelper {
    private static final Logger LOG = Logger.getLogger(PageValidateHelper.class);
    private static final String CONFIGFILE_NAME = "validation-mark.conf";
    private static final String WILDCARD = "*";
    /**
     * {@code (?<!exp) },零宽度负回顾后发断言,断言此位置的前面不能匹配表达式exp
     */
    private static final String SEPARATOR = "(?<!\\\\),";

    private static final PageValidateHelper instance = new PageValidateHelper();
    private static Thread watchDaemon;
    private final Map<String, Map<MarkType, Set<String>>> validations = new ConcurrentHashMap<>();
    private long lastReloadTime = Long.MIN_VALUE;

    public static boolean isValidationPage(String url, Document doc) {
        String domain = UrlHelper.getBaseDomain(url);
        Map<MarkType, Set<String>> marks = instance.getValidationMarks(domain);
        for (Map.Entry<MarkType, Set<String>> entry : marks.entrySet()) {
            MarkType type = entry.getKey();
            switch (type) {
                case HTML:
                    if (instance.isValidationPage(entry.getValue(), doc.html())) {
                        return true;
                    }
                    break;
                case SELECTOR:
                    if (instance.isValidationPage(entry.getValue(), doc)) {
                        return true;
                    }
                    break;
                default:
                    break;

            }
        }

        return false;
    }

    /**
     * 基于html文本判断
     */
    private boolean isValidationPage(Collection<String> marks, String html) {
        for (String mark : marks) {
            if (mark.equals("*")) {
                return true;
            }
            if (html.contains(mark)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 先用css selector选择指定区域,再基于该区域的文本判断
     */
    private boolean isValidationPage(Collection<String> marks, Document doc) {
        for (String mark : marks) {
            String[] items = mark.split(SEPARATOR, 2);
            if (items.length < 2) {
                continue;
            }
            String selector = items[0].trim().replace("\\,", ",");
            String fineMark = items[1].trim().replace("\\,", ",");
            if (doc.select(selector).text().contains(fineMark)) {
                return true;
            }
            // TODO: 16/5/4 当前只是判断css选择器得到的文本是否包含
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

    private Map<MarkType, Set<String>> getValidationMarks(String domain) {
        if (validations.isEmpty()) {
            try {
                ConfigFileManager.dumpIfNotExists(CONFIGFILE_NAME);
            } catch (IOException e) {
                LOG.error("Failed to dump " + CONFIGFILE_NAME, e);
                System.exit(-1);
                return validations.get(domain);
            }
            reload();
            startWatchDaemon();
        }
        Map<MarkType, Set<String>> marks = validations.get(domain);
        if (marks == null) {
            marks = new HashMap<>();
        }
        Map<MarkType, Set<String>> wild = validations.get(WILDCARD);
        if(wild != null) {
            marks.putAll(wild); // 添加通配的,即适用于所有网站的
        }
        return marks;
    }

    private void reload() {
        if (!needReload()) {
            return;
        }
        lastReloadTime = System.currentTimeMillis();

        Map<String, Map<MarkType, Set<String>>> tmpValidations = getValidationMarksConfiguration();
        validations.clear();
        validations.putAll(tmpValidations);
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

    private Map<String, Map<MarkType, Set<String>>> getValidationMarksConfiguration() {
        Map<String, Map<MarkType, Set<String>>> tmpValidations = new TreeMap<>();

        List<String> lines;
        try {
            lines = ConfigFileManager.readLines(CONFIGFILE_NAME);
        } catch (IOException e) {
            LOG.error("Failed to load " + CONFIGFILE_NAME);
            return validations;
        }

        for (String line : lines) {
            String myLine = line.trim();
            if (myLine.isEmpty() || myLine.startsWith("#")) {
                continue;
            }

            String[] items = myLine.split(SEPARATOR, 3);
            if (items.length < 3) {
                continue;
            }
            String site = items[0].trim().replace("\\,", ",");
            String typeStr = items[1].trim().replace("\\,", ",").toUpperCase();
            MarkType type = Enum.valueOf(MarkType.class, typeStr);
            String mark = items[2].trim();

            Map<MarkType, Set<String>> marks = tmpValidations.get(site);
            if (marks == null) {
                marks = new HashMap<>();
                tmpValidations.put(site, marks);
            }
            Set<String> fineMarks = marks.get(type);
            if (fineMarks == null) {
                fineMarks = new HashSet<>();
                marks.put(type, fineMarks);
            }
            fineMarks.add(mark);
        }
        return tmpValidations;
    }

    enum MarkType {
        /**
         * css selector
         */
        SELECTOR,
        /**
         * html text
         */
        HTML
    }
}
