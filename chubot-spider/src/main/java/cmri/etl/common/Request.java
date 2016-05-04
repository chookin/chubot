package cmri.etl.common;

import cmri.etl.downloader.Downloader;
import cmri.etl.downloader.HttpClientDownloader;
import cmri.etl.processor.PageProcessor;
import cmri.etl.validator.PageValidator;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.web.HttpMethod;
import cmri.utils.web.UrlHelper;
import com.google.gson.internal.Primitives;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.<br>
 * <p>
 * Created by zhuyin on 3/2/15.
 */
public class Request implements Comparable, Serializable {
    /**
     * Type of target resource.
     */
    TargetResource target = TargetResource.Doc;
    /**
     * Url of request web page.
     */
    private URL URL;

    private HttpMethod method = HttpMethod.GET;

    private final Map<String, String> headers = new HashMap<>();

    private final Map<String, String> cookies = new HashMap<>();

    private final Map<String, String> data = new HashMap<>();

    private String userAgent = ConfigManager.get("spider.web.userAgent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36");

    private int timeout = ConfigManager.getInt("spider.download.timeout", 15000);

    /**
     * Value is bigger, priority is high. Value range is [0,9].
     */
    private int priority = 6;

    /**
     * 所请求资源的有效时间;若该请求所下载的资源已经过期,那么不可再被使用
     */
    private Long validPeriod = ConfigManager.getLong("spider.page.validPeriod", Long.MAX_VALUE);

    /**
     * For this request, whether read cache or not.
     */
    private boolean cacheReadable = true;

    /**
     * Its custom page downloader.
     */
    private Downloader downloader = HttpClientDownloader.getInstance();

    /**
     * Its custom page processor.
     */
    private PageProcessor pageProcessor;

    /**
     * A validator to validate the fetched page resource is usable;
     */
    private PageValidator validator;

    /**
     * Store additional information in extras.
     */
    private final Map<String, Object> extras = new HashMap<>();

    /**
     * the file name of this request resource saved to. If not set, then auto generated when get called by {@link Request#getFilePath}.
     */
    private String filePath;

    /**
     * Count of request retry.
     */
    private int retryCount = -1;

    public Request() {
    }

    public Request(String url, PageProcessor processor) {
        setUrl(url);
        setPageProcessor(processor);
    }

    public String getUrl() {
        return URL.toString();
    }

    public URL getURL() {
        return URL;
    }

    /**
     * Creates a {@link URL} object from the {@link String} representation, and then set the request URL with the object.
     *
     * @param url the {@link String} to parse as a URL.
     * @return this
     * @throws IllegalArgumentException if no protocol is specified, or an
     *                                  unknown protocol is found, or {@code spec} is {@code null}.
     * @see java.net.URL#URL(java.net.URL, java.lang.String)
     */
    public Request setUrl(String url) {
        try {
            this.URL = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url: " + url + " " + e.getMessage());
        }
        return this;
    }

    /**
     * set the request method to use, GET or POST. Default is GET.
     */
    public Request setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public Request addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public Request addHeader(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Request addCookie(String key, String value) {
        this.cookies.put(key, value);
        return this;
    }

    /**
     * add cookies to be sent in the request
     */
    public Request addCookie(Map<String, String> cookie) {
        this.cookies.putAll(cookie);
        return this;
    }

    public Map<String, String> getCookies() {
        return this.cookies;
    }

    public Request addData(String key, String value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * add the supplied data to the request data parameters
     */
    public Request addData(Map<String, String> data) {
        this.data.putAll(data);
        return this;
    }

    public Map<String, String> getData() {
        return this.data;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public Request setDownloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public PageProcessor getPageProcessor() {
        return this.pageProcessor;
    }

    public Request setPageProcessor(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
        return this;
    }

    public PageValidator getValidator() {
        return validator;
    }

    public Request setValidator(PageValidator validator) {
        this.validator = validator;
        return this;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public Request setUserAgent(String userAgent) {
        Validate.notEmpty(userAgent, "invalid user-agent " + userAgent);
        this.userAgent = userAgent;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * 设置该请求的优先级
     *
     * @param priority 值越大,优先级越高;取值范围[0,9]
     * @return this
     */
    public Request setPriority(int priority) {
        if (priority < 0 || priority > 9) {
            throw new IllegalArgumentException("request priority should be less than 10 and bigger than -1");
        }
        this.priority = priority;
        return this;
    }

    /**
     * @return null if not find.
     */
    public Object getExtra(String key) {
        return extras.get(key);
    }

    public <T> T getExtra(String key, Class<T> classOfT) {
        return Primitives.wrap(classOfT).cast(extras.get(key));
    }

    public Map<String, Object> getExtra() {
        return extras;
    }

    /**
     * <ul>
     * <strong>Reserved extra key:</strong>
     * <p>
     * <li>charset, character set of file contents.</li>
     * <p>
     * </ul>
     */
    public Request putExtra(String key, Object value) {
        extras.put(key, value);
        return this;
    }

    public Request putExtra(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            putExtra(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * @return Unit is milliseconds.
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * Set the request timeouts (connect and read). If a timeout occurs, an IOException will be thrown. The default
     * timeout is 5 seconds (5000 millis). A timeout of zero is treated as an infinite timeout.
     *
     * @param millis number of milliseconds (thousandths of a second) before timing out connects or reads.
     * @return this
     */
    public Request setTimeout(int millis) {
        this.timeout = millis;
        return this;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Request incrRetryCount() {
        ++retryCount;
        return this;
    }


    /**
     * 设置所请求的资源的有效期,过期后将不会被缓存读取;当设置有效为0时,则不再使用本地缓存,也不保存本次请求所下载的资源
     *
     * @param millis 有效期,单位毫秒
     * @return this
     */
    public Request setValidPeriod(long millis) {
        if (millis < 0 || millis == Long.MAX_VALUE) {
            this.validPeriod = Long.MAX_VALUE;
        } else {
            this.validPeriod = millis;
        }
        return this;
    }

    public Long getValidPeriod() {
        return this.validPeriod;
    }

    public boolean isCacheReadable() {
        return cacheReadable;
    }

    public void setCacheReadable(boolean cacheReadable) {
        this.cacheReadable = cacheReadable;
    }

    public TargetResource getTarget() {
        return this.target;
    }

    public Request setTarget(TargetResource target) {
        this.target = target;
        return this;
    }

    /**
     * Set the file name of this request resource saved to. If not set, use UrlHelper to generate, such as
     * case Doc:  return UrlHelper.getFilePath(url)+".htm";
     * case Json: return UrlHelper.getFilePath(url)+".json";
     * case File: return UrlHelper.getBinaryFilePath(url);
     *
     * @param filePath file name to use
     * @return this
     */
    public Request setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    /**
     * @return the file name of this request resource saved to
     */
    public String getFilePath() {
        if (StringUtils.isNotEmpty(filePath)) {
            return filePath;
        }
        switch (target) {
            case Doc:
                return UrlHelper.getFilePath(getUrl()) + ".htm";
            case Json:
                return UrlHelper.getFilePath(getUrl()) + ".json";
            case File:
                return UrlHelper.getBinaryFilePath(getUrl());
            default:
                throw new NotImplementedException();

        }
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!(o instanceof Request)) throw new ClassCastException();

        Request request = (Request) o;
        return getUrl().compareTo(request.getUrl());// todo extras compare
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;

        Request request = (Request) o;
        return this.URL.equals(request.getURL()) && extras.equals(request.extras);

    }

    @Override
    public int hashCode() {
        int result = URL.hashCode();
        result = 31 * result + extras.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Request{" +
                "target=" + target +
                ", URL=" + URL +
                ", method=" + method +
                ", headers=" + headers +
                ", cookies=" + cookies +
                ", data=" + data +
                ", userAgent='" + userAgent + '\'' +
                ", timeout=" + timeout +
                ", priority=" + priority +
                ", validPeriod=" + validPeriod +
                ", downloader=" + downloader +
                ", pageProcessor=" + pageProcessor +
                ", extras=" + extras +
                ", filePath='" + filePath + '\'' +
                ", retryCount=" + retryCount +
                '}';
    }

    /**
     * 目标资源类型
     */
    public enum TargetResource implements Serializable {
        /**
         * html 文档
         */
        Doc,
        /**
         * Json文本
         */
        Json,
        /**
         * 二进制
         */
        ByteArray,
        File
    }

    /**
     * 由url生成request
     */
    public interface RequestGenerator {
        Request generate(String url);
    }
}
