package cmri.etl.spider.monitor;

import cmri.etl.spider.Spider;
import cmri.utils.configuration.ConfigManager;
import com.sun.jdmk.comm.HtmlAdaptorServer;
import org.apache.log4j.Logger;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by zhuyin on 5/24/15.
 */
public interface SpiderMonitor {
    static SpiderMonitor instance() {
        return SpiderMonitorAdapter.INSTANCE;
    }

    /**
     * Register spider for monitor.
     * @return status bean bind to {@code spider}
     */
    SpiderMetricMBean register(Spider spider) throws JMException;

    class SpiderMonitorAdapter implements SpiderMonitor{
        private static final Logger LOG = Logger.getLogger(SpiderMonitorAdapter.class);
        private final MBeanServer mbeanServer;
        private final String jmxServerName = "feisier";
        private final Set<SpiderMetricMBean> spiderStatuses = new CopyOnWriteArraySet<>();
        private HtmlAdaptorServer adapter;
        private Thread watchDaemon;

        static final SpiderMonitorAdapter INSTANCE = new SpiderMonitorAdapter();

        protected SpiderMonitorAdapter() {
            // ManagementFactory.getPlatformMBeanServer() returns a reference to the existing MBean server within the JVM. JConsole looks at the beans on that server.
            // If you use MBeanServerFactory.createMBeanServer(), that will create an entirely new server. JConsole has no knowledge of it, and so will not see the beans registered with it.
            mbeanServer = ManagementFactory.getPlatformMBeanServer();
            // mbeanServer = MBeanServerFactory.createMBeanServer();
        }

        private void startHtmlAdapter() {
            try {
                int jmxPort = ConfigManager.getInt("spider.jmx.port", 8082);
                ObjectName adapterName = new ObjectName(jmxServerName + ":name=htmlAdapter,port=" + jmxPort);
                adapter = new HtmlAdaptorServer(jmxPort);
                mbeanServer.registerMBean(adapter, adapterName);
                adapter.start();
                LOG.info("start jmx web server at port " + jmxPort);
                // adapter.invoke(adapterName, "start", null, null);
            } catch (MalformedObjectNameException | NotCompliantMBeanException | MBeanRegistrationException | InstanceAlreadyExistsException e) {
                LOG.error(null, e);
            }
        }

        private void stopHtmlAdapter() {
            if (adapter != null) {
                adapter.stop();
            }
        }

        private synchronized void startWatchDaemon() {
            if (watchDaemon != null) {
                return;
            }
            startHtmlAdapter();
            watchDaemon = new Thread() {
                @Override
                public void run() {
                    boolean allStop;
                    do {
                        allStop = true;
                        for (SpiderMetricMBean bean : spiderStatuses) {
                            Spider.Status status = Enum.valueOf(Spider.Status.class, bean.getStatus());
                            if(status.compareTo(Spider.Status.Stop) < 0){
                                allStop = false;
                                break;
                            }
                        }
                    } while (!allStop);
                    stopHtmlAdapter();
                }
            };
            // 即使设置为守护进程也没有用,因为HtmlAdaptorServer线程在运行着
            watchDaemon.setDaemon(true);
            watchDaemon.start();
        }

        /**
         * Register spider for monitor.
         */
        public SpiderMetricMBean register(Spider spider) throws JMException {
            MonitorSpiderListener listener = new MonitorSpiderListener(spider);

            // create mxbean and register
            SpiderMetricMBean spiderStatus = new SpiderMetric(listener);
            registerMBean(spiderStatus);
            spiderStatuses.add(spiderStatus);
            startWatchDaemon();
            return spiderStatus;
        }

        protected void registerMBean(SpiderMetricMBean spiderStatus) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
            // 首先要给被绑定的类起一个名字,然后把这个名字和被管理的类一起注册到MBeanServer当中
            ObjectName objName = new ObjectName(jmxServerName + ":name=" + spiderStatus.getName()+"-"+spiderStatus.getUUID());
            mbeanServer.registerMBean(spiderStatus, objName);
        }
    }
}
