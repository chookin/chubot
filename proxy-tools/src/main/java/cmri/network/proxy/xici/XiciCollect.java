package cmri.network.proxy.xici;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.downloader.CasperJsDownloader;
import cmri.etl.processor.PageProcessor;
import cmri.etl.proxy.Proxy;
import cmri.network.proxy.service.ProxyCollect;
import cmri.utils.lang.TimeHelper;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhuyin on 3/25/15.
 */
public class XiciCollect extends ProxyCollect {
    @Override
    public Collection<Request> getSeedProxyRequests() {
        return ProxyPageProcessor.getSeedRequests();
    }

    public static void main(String[] args){
        new XiciCollect().setArgs(args).action();
    }

    static class ProxyPageProcessor implements PageProcessor {
        private static ProxyPageProcessor processor = new ProxyPageProcessor();

        public static Collection<Request> getSeedRequests(){
            Set<Request> requests = new HashSet<>();
            requests.add(new Request("http://www.xici.net.co/nn/", processor)
                    .setDownloader(CasperJsDownloader.getInstance())
            ); // 国内高匿代理
            requests.add(new Request("http://www.xici.net.co/wn/", processor)
                    .setDownloader(CasperJsDownloader.getInstance())
            ); // 国外高匿代理
            requests.add(new Request("http://www.xici.net.co/nt/", processor)
                    .setDownloader(CasperJsDownloader.getInstance())
            );// 国内普通代理
            requests.add(new Request("http://www.xici.net.co/wt/", processor)
                    .setDownloader(CasperJsDownloader.getInstance())
            ); // 国外普通代理
            return requests;
        }

        @Override
        public void process(ResultItems page) {
            Document doc = (Document) page.getResource();
            String startUrl = page.getRequest().getUrl();
            Elements nextElements = doc.select("#body > div.pagination > a");
            String strTotalPage = nextElements.get(nextElements.size() - 2).text().trim();
            int totalPage = Integer.parseInt(strTotalPage);
            for (int i = 1; i <= totalPage; ++i) {
                String url = String.format("%s/%d", startUrl, i);
                page.addTargetRequest(ProxyPageProcessor2.getRequest(url));
            }
        }
    }

    static  class ProxyPageProcessor2 implements PageProcessor {
        private static final Logger LOG = Logger.getLogger(ProxyPageProcessor2.class);
        private static final ProxyPageProcessor2 processor = new ProxyPageProcessor2();
        public static Request getRequest(String url){
            return new Request(url, processor)
                    .setDownloader(CasperJsDownloader.getInstance());
        }

        @Override
        public void process(ResultItems page) {
            Document doc = (Document) page.getResource();

            Elements elements = doc.select("#ip_list > tbody > tr");
            int count = 0;
            for (Element element : elements) {
                if (++count == 1) {
                    continue;
                }
                Proxy proxy = new Proxy()
                        .setHost(getIP(element))
                        .setPort(getPort(element))
                        .set("country", getCountry(element))
                        .set("location", getLocation(element))
                        .set("highAnonymity", isHighAnonymity(element))
                        .set("type", getType(element)) // HTTP or HTTPS.
                        .set("accessTime", getSpeed(element)) // Time usage to access, in seconds.
                        .set("connectTime", getConnectionTime(element)) // Time usage to establish connection, in seconds
                        .set("validateTime", getValidateTime(element)); // Time of validate this proxy usability.
                LOG.trace(proxy);
                page.addItem(proxy);
            }
        }

        String getCountry(Element element) {
            // <td><img alt="Cn" src="http://fs.xicidaili.com/images/flag/cn.png" /></td>
            String str = element.select("td:nth-child(2) > img").attr("src");
            return str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf("."));
        }

        String getIP(Element element) {
            return element.select("td:nth-child(3)").text().trim();
        }

        Integer getPort(Element element) {
            String port = element.select("td:nth-child(4)").text().trim();
            return Integer.parseInt(port);
        }

        String getLocation(Element element) {
            return element.select("td:nth-child(5)").text().trim();
        }

        boolean isHighAnonymity(Element element) {
            String str = element.select("td:nth-child(6)").text().trim();
            return str.equals("高匿");
        }

        String getType(Element element) {
            return element.select("td:nth-child(7)").text().trim();
        }

        Double getSpeed(Element element) {
            String str = element.select("td:nth-child(8) div").attr("title").trim();
            if (str.endsWith("秒")) {
                str = str.substring(0, str.length() - 2);
                return Double.parseDouble(str);
            } else {
                return null;
            }
        }

        Double getConnectionTime(Element element) {
            String str = element.select("td:nth-child(9) div").attr("title").trim();
            if (str.endsWith("秒")) {
                str = str.substring(0, str.length() - 2);
                return Double.parseDouble(str);
            } else {
                return null;
            }
        }

        Date getValidateTime(Element element) {
            String str = element.select("td:nth-child(10)").text();
            // 15-02-12 09:59
            str = "20" + str;
            return TimeHelper.parseDate(str, "yyyy-MM-dd H:m");
        }
    }
}
