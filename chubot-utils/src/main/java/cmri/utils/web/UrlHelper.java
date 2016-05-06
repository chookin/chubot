package cmri.utils.web;

import cmri.utils.configuration.ConfigManager;
import cmri.utils.io.FileHelper;
import cmri.utils.lang.StringHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;

/**
 * Created by zhuyin on 3/2/14.
 */
public class UrlHelper {
    private static final String localPath = new File(ConfigManager.get("spider.download.directory", "/tmp")).getAbsolutePath();
    private static final String[] documentExtensions = {"", ".htm", ".html"};
    private static final String protocolRegex = "(?i)^([a-zA-Z]+)://";
    private static boolean ignorePound = true;

    private UrlHelper() {
    }

    public static String getProtocolRegex() {
        return protocolRegex;
    }

    /**
     * erase the protocol of a URL and return the erased.
     *
     * @param url
     * @return
     */
    public static String eraseProtocol(String url) {
        if (url == null) {
            return null;
        }
        return url.replaceAll(String.format("%s", UrlHelper.getProtocolRegex()), "");
    }

    public static String eraseProtocolAndStart3W(String url) {
        if (url == null) {
            return null;
        }
        // (?i)让表达式忽略大小写进行匹配;
        // '^'和'$'分别匹配字符串的开始和结束
        return eraseProtocol(url).replaceAll("(?i)(^www.)", "");
    }

    /**
     * erase the end "/" and inner page jump.
     *
     * @param url
     * @return
     */
    public static String trimUrl(String url) {
        Validate.notNull(url);
        String myUrl = StringUtils.strip(url);
        if (myUrl.endsWith("/")) {
            myUrl = myUrl.substring(0, myUrl.length() - 1);
        }
        if (ignorePound) {
            myUrl = trimUrlPoundSuffix(myUrl);
        }
        if (myUrl.compareTo(url) == 0) {
            return myUrl;
        }
        return trimUrl(myUrl);
    }

    /**
     * Is equals after url trim.
     */
    public static boolean isUrlEqual(String lhs, String rhs) {
        String myLhs = UrlHelper.trimUrl(lhs);
        String myRhs = UrlHelper.trimUrl(rhs);
        return myLhs.compareTo(myRhs) == 0;
    }

    public static boolean isHrefEqualIgnoreCase(String lhs, String rhs) {
        String myLhs = UrlHelper.trimUrl(lhs);
        String myRhs = UrlHelper.trimUrl(rhs);
        return myLhs.compareToIgnoreCase(myRhs) == 0;
    }

    /**
     * "#" indicates inner page jump mostly, so may be you need to trim strings
     * behind "#"
     *
     * @param url
     * @return
     */
    private static String trimUrlPoundSuffix(String url) {
        org.jsoup.helper.Validate.notNull(url);
        int indexPound = url.indexOf("#");
        if (indexPound == -1) {
            return url;
        }
        return trimUrl(url.substring(0, indexPound));
    }

    /**
     * judge whether resource indicated by the URL is a HTML page by the URL's
     * extension
     */
    public static boolean isHtmlPage(String url) {
        String extension = getExtension(url);
        for (String item : documentExtensions) {
            if (extension.compareTo(item) == 0) {
                return true;
            }
        }
        return false;

    }

    public static String getExtension(String url) {
        url = url.replace('\\', '/');
        int indexLastSlash = url.lastIndexOf('/');
        if (indexLastSlash == -1) {
            indexLastSlash = 0;
        }
        int indexLastDot = url.substring(indexLastSlash).lastIndexOf('.');
        if (indexLastDot == -1) {
            return "";
        } else {
            return url.substring(indexLastDot).toLowerCase();
        }
    }

    public static String getHost(String url) {
        if (url == null) {
            return null;
        }
        String myUrl = eraseProtocolAndStart3W(url).toLowerCase();
        int firstSlashIndex = myUrl.indexOf('/');
        if (firstSlashIndex == -1) {
            return myUrl;
        }
        return myUrl.substring(0, firstSlashIndex);
    }

    public static String getBaseDomain(String url) {
        if (url == null) {
            return null;
        }
        return StringHelper.parseRegex(url, "(^[a-zA-Z]+://)?(www.)?([\\w\\.]+)/*", 3);
    }

    /**
     * Get path, with no protocol, no query parameters. For examples, path of "http://snapshot:8080/materials/homepages?sig=0ae7ea9680aeedfb77c2069fd14bd1fe&time=1450193196206" is "/materials/homepages"
     */
    public static String getPath(String url){
        if (url == null) {
            return null;
        }
        /*
        group 0, url
        group 1, protocol
        group 2, www
        group 3, domain
        group 4, path
        group ..., query parameters
         */
        return StringHelper.parseRegex(url, "(^[a-zA-Z]+://)?(www.)?([\\w\\.]+)(/+[\\w\\.\\/]+)\\?*", 4);
    }
    public static String getProtocol(String url) {
        return StringHelper.parseRegex(url, protocolRegex, 1);
    }

    public static String getHash(String url){
        String regex = "(?i)^[a-zA-Z]+://(www.)*([a-zA-Z\\d\\.]+/?[a-zA-Z\\d]+)(/[\\s\\S]*)";
        String domain = StringHelper.parseRegex(url, regex, 2); // domain + followed path
        if(domain == null){// such as 'http://www.126.com', not contains extra '/'
            regex = "(?i)^[a-zA-Z]+://(www.)*([a-zA-Z\\d\\.]+/?[a-zA-Z\\d]+)([\\s\\S]*)*";
            domain = StringHelper.parseRegex(url, regex, 2);
        }
        return domain + "/" + DigestUtils.md5Hex(url);
    }

    public static String getLocalPath(){
        return localPath;
    }
    /**
     * Encode url string to md5 string, and prefix localPath.
     */
    public static String getFilePath(String url) {
        return String.format("%s/%s", localPath, getHash(url));
    }

    /**
     * Format url string, such as replace '&' to '-', remove end '/', and prefix localPath.
     */
    public static String getBinaryFilePath(String url) {
        String filePath = UrlHelper.eraseProtocolAndStart3W(url);
        filePath = FileHelper.formatFileName(filePath);
        filePath = FilenameUtils.normalizeNoEndSeparator(filePath);
        return String.format("%s/%s", localPath, filePath);
    }

}
