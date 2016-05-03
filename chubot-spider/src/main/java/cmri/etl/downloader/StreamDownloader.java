package cmri.etl.downloader;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.spider.Spider;
import cmri.utils.lang.StringHelper;
import cmri.utils.web.HttpMethod;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by zhuyin on 3/9/15.
 */
public class StreamDownloader implements Downloader {
    private static final StreamDownloader instance = new StreamDownloader();

    public static StreamDownloader getInstance() {
        return instance;
    }

    private InputStream getInputStream(Request request, Spider spider) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) request.getURL().openConnection(spider.getProxy(request).getJavaProxy());
        String userAgent = request.getUserAgent();
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setConnectTimeout(request.getTimeout());
        conn.setReadTimeout(request.getTimeout());
        if (HttpMethod.POST.equals(request.getMethod()))
            conn.setDoOutput(true);
        conn.addRequestProperty("Cookie", StringHelper.join(request.getCookies(), "=", ";"));

        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            conn.addRequestProperty(entry.getKey(), entry.getValue());
        }
        return conn.getInputStream();
    }

    String downloadText(Request request, Spider spider) throws IOException {
        InputStream stream = getInputStream(request, spider);
        String line;
        StringBuilder strb = new StringBuilder();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
            while ((line = in.readLine()) != null) {
                strb.append(line);
            }
        }
        return strb.toString();
    }

    /**
     * 获取比特流并作为结果返回
     */
    byte[] downloadByteArray(Request request, Spider spider)
            throws IOException {
        try(InputStream in = getInputStream(request, spider)) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096000]; // 4M
                do {
                    int bytesRead = in.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }
                    out.write(buffer, 0, bytesRead);
                } while (true);
                return out.toByteArray();
            }
        }
    }

    /**
     * 下载文件到本地,文件名由Request#getFilePath()决定
     *
     * @return file name.
     */
    String downloadFile(Request request, Spider spider)
            throws IOException {
        String fileName = request.getFilePath();
        try(InputStream stream = getInputStream(request, spider)) {
            try (OutputStream out = new FileOutputStream(new File(fileName))) {
                byte[] buffer = new byte[4096000]; // 4M
                do {
                    int bytesRead = stream.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }
                    out.write(buffer, 0, bytesRead);
                } while (true);

                return fileName;
            }
        }
    }

    @Override
    public ResultItems download(Request request, Spider spider) throws IOException {
        Object resource = null;
        switch (request.getTarget()) {
            case Doc:
                resource = Jsoup.parse(downloadText(request, spider), request.getUrl());
                break;
            case Json:
                resource = downloadText(request, spider);
                break;
            case File:
                resource = downloadFile(request, spider);
                break;
            case ByteArray:
                resource = downloadByteArray(request, spider);
                break;
        }
        return new ResultItems(request, spider).setResource(resource);
    }
}
