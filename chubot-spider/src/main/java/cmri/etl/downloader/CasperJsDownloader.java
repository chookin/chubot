package cmri.etl.downloader;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.dao.UrlFetchedDAO;
import cmri.etl.proxy.Proxy;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.io.FileHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyin on 3/3/15.
 * CapserJsDownload is used to download html page rendering by js.
 */
public class CasperJsDownloader implements Downloader {
    static final CasperJsDownloader instance = new CasperJsDownloader();
    public static CasperJsDownloader getInstance(){return instance;}
    protected CasperJsDownloader(){}

    private String script = "js/downloader.js";

    public Downloader setScript(String script) {
        this.script = script;
        return this;
    }

    public String getScript(){
        return script;
    }
    /**
     * Execute system processes with Java ProcessBuilder and Process.
     * http://alvinalexander.com/java/java-exec-processbuilder-process-1
     *
     * @return downloader process.
     * @throws IOException
     */
    private Process getDownloaderProcess(Request request, Spider spider) throws IOException {
        String url = request.getUrl();
        List<String> command = new ArrayList<>();
        command.add("casperjs");
        command.add(getJsPath(this.script));
        command.add(String.format("--url=%s", url));
        command.add(String.format("--out=%s", request.getFilePath()));
        command.add(String.format("--userAgent=%s", request.getUserAgent()));

        Proxy myProxy = spider.getProxy(request);
        if (myProxy != null) {
            String host = myProxy.getHost();
            int port = myProxy.getPort();
            command.add(String.format("--proxy=%s:%d", host, port));
        }
        getLogger().trace("exec cmd: " + command + " for download " + url);
        return new ProcessBuilder(command).start();
    }

    /**
     * @return true if success to download.
     */
    protected boolean doDownload(Request request, Spider spider) throws IOException {
        String url = request.getUrl();
        String out = request.getFilePath();
        FileHelper.makeParentDirs(out);
        Process proc = getDownloaderProcess(request, spider);
        try {
            try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                long start = System.currentTimeMillis();
                int timeout = 60000;
                if (request.getTimeout() > timeout) { // Notice: for casperjs, timeout should be long.
                    timeout = request.getTimeout();
                }
                String s;
                while ((s = stdInput.readLine()) != null) {
                    getLogger().debug(s);
                    if (System.currentTimeMillis() > start + timeout) {
                        getLogger().error("time out to download " + url);
                        return false;
                    }
                }
            }
            getLogger().trace("success to download " + url);
            return true;
        }finally {
            proc.destroy();
        }
    }

    /**
     * @param jsFile js file name
     * @return absolute path name of the js script file.
     * @throws java.io.IOException
     */
    public static String getJsPath(String jsFile) throws IOException {
        String path = jsFile;
        File file = new File(path);
        path = file.getAbsolutePath();
        if (file.exists()) { // when debug, its path is ${MAVEN_M2_PATH}/repository/js/
            return path;
        } else {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(jsFile);
            FileHelper.save(in, path);
            return path;
        }
    }

    public Document getDocument(String url, String userAgent) throws IOException {
        return (Document) download(
                new Request().setUrl(url).setUserAgent(userAgent),
                new SpiderAdapter()
        ).getResource();
    }

    @Override
    public ResultItems download(Request request, Spider spider) throws IOException {
        getLogger().trace("get " + request.getUrl());

        ResultItems resultItems = useCache(request, spider);
        if(resultItems != null){
            return resultItems;
        }

        switch (request.getTarget()){
            case Doc:
                return myDownload(request, spider);
            default:
                return JsoupDownloader.getInstance().download(request, spider);
        }
    }

    private ResultItems myDownload(Request request, Spider spider) throws IOException{
        ResultItems resultItems = new ResultItems(request, spider);
        if(doDownload(request, spider)) {
            Document doc = Jsoup.parse(new File(request.getFilePath()), "utf-8", request.getUrl());
            resultItems.setResource(doc)
                    .setField("skipArchive", true);
            UrlFetchedDAO.save(request.getUrl());
        }
        return resultItems;
    }
}
