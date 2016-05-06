package cmri.etl.downloader;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.spider.Spider;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuyin on 7/3/15.
 */
public class HttpClientDownloader implements Downloader {
    private Logger LOG = LoggerFactory.getLogger(getClass());
    static final HttpClientDownloader instance = new HttpClientDownloader();
    public static HttpClientDownloader getInstance(){return instance;}
    protected HttpClientDownloader(){}

    private CloseableHttpClient getHttpClient(Request request, Spider spider) {
        return new HttpClientGenerator().getClient(request, spider);
    }

    @Override
    public ResultItems download(Request request, Spider spider) throws IOException{
        getLogger().trace("get " + request.getUrl());
        ResultItems resultItems = useCache(request, spider);
        if(resultItems != null){
            return resultItems;
        }

        CloseableHttpResponse httpResponse = null;
        try {
            HttpUriRequest httpUriRequest = getHttpUriRequest(request, spider);
            httpResponse = getHttpClient(request, spider).execute(httpUriRequest);
            return handleResponse(request, spider, httpResponse);
        } finally {
            try {
                if (httpResponse != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                LOG.warn("close response fail", e);
            }
        }
    }

    protected HttpUriRequest getHttpUriRequest(Request request, Spider spider) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());

        for (Map.Entry<String, String> headerEntry : request.getHeaders().entrySet()) {
            requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(request.getTimeout())
                .setSocketTimeout(request.getTimeout())
                .setConnectTimeout(request.getTimeout())
                .setCookieSpec(CookieSpecs.BEST_MATCH);
        HttpHost host = spider.getProxy(request).getHttpHost();
        requestConfigBuilder.setProxy(host);
        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    protected RequestBuilder selectRequestMethod(Request request) {
        switch (request.getMethod()){
            case GET:
                return RequestBuilder.get();
            case POST:
                RequestBuilder requestBuilder = RequestBuilder.post();
                for(Map.Entry<String,String> entry: request.getData().entrySet()){
                    requestBuilder.addParameter(entry.getKey(), entry.getValue());
                }
                return requestBuilder;
            case HEAD:
                return RequestBuilder.head();
            case PUT:
                return RequestBuilder.put();
            case DELETE:
                return RequestBuilder.delete();
            case TRACE:
                return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + request.getMethod());
    }

    protected ResultItems handleResponse(Request request, Spider spider, HttpResponse httpResponse) throws IOException {
        ResultItems resultItems = new ResultItems(request, spider);
        switch (request.getTarget()){
            case ByteArray:
                return resultItems.setResource(IOUtils.toByteArray(httpResponse.getEntity().getContent()));
            case Json:
                return resultItems.setResource(getContent(request, httpResponse));
            case Doc:
                return resultItems.setResource(Jsoup.parse(getContent(request, httpResponse), request.getUrl()));
            case File:
            default:
                // TODO
                throw new NotImplementedException();
        }
    }

    protected String getContent(Request request, HttpResponse httpResponse) throws IOException {
        String charset = (String) request.getExtra("charset");
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            charset = getHtmlCharset(httpResponse, contentBytes);
            return new String(contentBytes, charset);
        } else {
            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
    }

    protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset="";
        // charset
        // 1、encoding in http header Content-Type
        Header contentType = httpResponse.getEntity()
                .getContentType();
        if(contentType != null) {
            String value = contentType.getValue();
            charset = getCharsetFromContentType(value);
            if (StringUtils.isNotBlank(charset)) {
                // LOG.debug("Auto get charset: {}", charset);
                return charset;
            }
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        // 2、charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.contains("charset")) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    if(charset == null){
                        throw new RuntimeException("fail to parse charset from meta "+ metaContent);
                    }
                    break;
                }
                // 2.2、html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        return charset;
    }

    private static final Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)");

    public static String getCharsetFromContentType(String contentType) {
        Matcher matcher = patternForCharset.matcher(contentType);
        if (matcher.find()) {
            String charset = matcher.group(1);
            if (Charset.isSupported(charset)) {
                return charset;
            }
        }
        return null;
    }

    static class HttpClientGenerator{

        private PoolingHttpClientConnectionManager connectionManager;

        public HttpClientGenerator() {
            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", SSLConnectionSocketFactory.getSocketFactory())
                    .build();
            connectionManager = new PoolingHttpClientConnectionManager(reg);
            connectionManager.setDefaultMaxPerRoute(100);
        }

        public HttpClientGenerator setPoolSize(int poolSize) {
            connectionManager.setMaxTotal(poolSize);
            return this;
        }

        public CloseableHttpClient getClient(Request request, Spider spider) {
            return generateClient(request);
        }

        private CloseableHttpClient generateClient(Request request) {
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
            httpClientBuilder.setUserAgent(request.getUserAgent());
            SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
            httpClientBuilder.setDefaultSocketConfig(socketConfig);
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, true));
            generateCookie(httpClientBuilder, request);
            return httpClientBuilder.build();
        }

        private void generateCookie(HttpClientBuilder httpClientBuilder, Request request) {
            CookieStore cookieStore = new BasicCookieStore();
            for (Map.Entry<String, String> cookieEntry : request.getCookies().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookieStore.addCookie(cookie);
            }
            httpClientBuilder.setDefaultCookieStore(cookieStore);
        }
    }
}
