package cmri.etl.downloader;

import cmri.etl.proxy.Proxy;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.spider.Spider;
import cmri.utils.io.FileHelper;
import org.jsoup.Jsoup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhuyin on 3/8/15.
 * <p/>
 * <p/>
 * https://sites.google.com/a/chromium.org/chromedriver/getting-started
 * 1. Download chrome driver, http://chromedriver.storage.googleapis.com/2.14/chromedriver_linux64.zip
 * 2. Update google-chrome > v39
 * wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm
 * 3. Set chrome driver's path to java system property: System.setProperty("webdriver.chrome.driver","/home/work/chromedriver");
 */
public class SeleniumDownloader implements Downloader {
    static final SeleniumDownloader instance = new SeleniumDownloader();
    public static SeleniumDownloader getInstance(){return instance;}
    protected SeleniumDownloader() {
        try {
            dumpDriveIfNotExists();
            new File(driverName).setExecutable(true);
        } catch (IOException e) {
            getLogger().error(null, e);
            System.exit(-1);
        }
        // if not specified, WebDriver will search your path for chromedriver.
        System.setProperty("webdriver.chrome.driver", driverName);
    }

    String driverName = "chromedriver";

    private void dumpDriveIfNotExists() throws IOException {
        if(new File(driverName).isFile()){
            return;
        }
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(driverName);
        FileHelper.save(in, driverName);
    }
    @Override
    public ResultItems download(Request request, Spider spider) throws IOException {
        getLogger().trace("get " + request.getUrl());

        ResultItems resultItems = useCache(request, spider);
        if(resultItems != null){
            return resultItems;
        }

        switch (request.getTarget()) {
            case Doc:
                return myDownload(request, spider);
            default:
                return JsoupDownloader.getInstance().download(request, spider);
        }
    }

    private ResultItems myDownload(Request request, Spider spider) throws IOException{
        ResultItems resultItems = new ResultItems(request, spider);

        DesiredCapabilities cap = DesiredCapabilities.chrome();
        ChromeOptions co = new ChromeOptions();
        String userAgent = request.getUserAgent();
        co.addArguments("--user-agent=" + userAgent);
        cap.setCapability(ChromeOptions.CAPABILITY, co);

        Proxy myProxy = spider.getProxy(request);
        if (myProxy != null) {
            String proxy = myProxy.getHost() + ":" + myProxy.getPort();
            org.openqa.selenium.Proxy p = new org.openqa.selenium.Proxy();
            p.setHttpProxy(proxy).setFtpProxy(proxy).setSslProxy(proxy);
            cap.setCapability(CapabilityType.PROXY, p);
        }

        WebDriver driver = new ChromeDriver(cap);
        driver.get(request.getUrl());
        // Get the html source of the page
        String pageSource = driver.getPageSource();
        //Close the browser
        driver.quit();
        return resultItems.setResource(Jsoup.parse(pageSource, request.getUrl()));
    }
}
