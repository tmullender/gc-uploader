package co.escapeideas.gc.uploader;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 31/03/14
 * Time: 23:32
 */
public class App implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private final Login login;
    private final Uploader uploader;
    private final Configuration configuration;
    private final Watcher watcher;

    public App(Configuration configuration) {
        this.configuration = configuration;
        final HttpClient httpClient = createClient(configuration);
        login = new Login(httpClient);
        uploader = new Uploader(configuration, httpClient);
        watcher = new Watcher(configuration);
    }

    private HttpClient createClient(Configuration configuration) {
        final RequestConfig config = RequestConfig.custom()
                .setCircularRedirectsAllowed(true)
                .setConnectionRequestTimeout(configuration.getConnectTimeout())
                .setConnectTimeout(configuration.getConnectTimeout())
                .setSocketTimeout(configuration.getConnectTimeout())
                .setStaleConnectionCheckEnabled(true)
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setDefaultCookieStore(new BasicCookieStore())
                .build();
    }

    @Override
    public void run() {
        logger.info("Running");
        try {
            final File[] newFiles = watcher.findNewFiles();
            if (newFiles.length > 0) {
                login.login(configuration.getUsername(), configuration.getPassword());
                for (File file : newFiles){
                    uploader.upload(file);
                }
            }
        } catch (Exception e) {
            logger.error("run", e);
        }
        logger.info("Run complete");
    }

    public void start() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        if (configuration.getPassword() == null){
            logger.error("No password has been provided, skipping start");
        } else {
            final Integer interval = configuration.getCheckInterval();
            logger.info("Starting, interval: {}", interval);
            scheduledExecutorService.scheduleWithFixedDelay(this, 0, interval, TimeUnit.MINUTES);
        }
    }

    public static void main(String... args){
        final String path;
        if (args.length > 0){
            path = args[0];
        } else {
            path = PropertiesConfiguration.DEFAULT_APPLICATION_DIRECTORY + "config.properties";
        }
        createApp(path).start();
    }

    private static App createApp(String path) {
        return new App(new PropertiesConfiguration(new File(path)));
    }


}
