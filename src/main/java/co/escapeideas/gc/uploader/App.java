package co.escapeideas.gc.uploader;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 31/03/14
 * Time: 23:32
 * To change this template use File | Settings | File Templates.
 */
public class App implements Runnable{

    private static final Logger logger = Logger.getLogger(App.class.getName());

    private final Login login;
    private final Uploader uploader;
    private final Configuration configuration;
    private final Watcher watcher;

    public App(Configuration configuration) {
        this.configuration = configuration;
        final CloseableHttpClient httpClient = createClient();
        login = new Login(httpClient);
        uploader = new Uploader(httpClient);
        watcher = new Watcher(configuration);
    }

    private CloseableHttpClient createClient() {
        return HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
    }

    @Override
    public void run() {
        logger.info("Running");
        try {
            login.login(configuration.getUsername(), configuration.getPassword());
            for (File file : watcher.findNewFiles()){
                uploader.upload(file);
            }
        } catch (Exception e) {
            logger.throwing(App.class.getName(), "run", e);
        }
        logger.info("Run complete");
    }

    public void start() {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(this, 0, 1, TimeUnit.MINUTES);
    }

    public static void main(String... args){
        final String path;
        if (args.length > 0){
            path = args[0];
        } else {
            path = "~/.gc-uploader.properties";
        }
        createApp(path).start();
    }

    private static App createApp(String path) {
        return new App(new PropertiesConfiguration(new File(path)));
    }


}
