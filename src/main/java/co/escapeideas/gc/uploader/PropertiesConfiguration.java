package co.escapeideas.gc.uploader;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 01/04/14
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesConfiguration implements Configuration {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfiguration.class);
    public static final String DEFAULT_APPLICATION_DIRECTORY = System.getProperty("user.home") + "/.gc-uploader/";

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String ACCEPTABLE_EXTENSIONS = "acceptable.extensions";
    private static final String NEW_DIRECTORY = "upload.new.directory";
    private static final String COMPLETE_DIRECTORY = "upload.complete.directory";
    private static final String ERROR_DIRECTORY = "upload.error.directory";
    private static final String CHECK_INTERVAL = "new.file.check.interval";
    private static final String CONNECT_TIMEOUT = "http.connect.timeout";
    private static final Integer DEFAULT_CHECK_INTERVAL = 5;
    private static final Integer DEFAULT_CONNECT_TIMEOUT = 30000;

    private final Properties properties;

    public PropertiesConfiguration() {
        properties = createDefaults();
    }

    public PropertiesConfiguration(File file) {
        this();
        loadProperties(file);
    }

    private void loadProperties(final File file) {
        if (file != null && file.exists()) {
            try {
                properties.load(new FileInputStream(file));
            } catch (IOException e) {
                logger.error("init", e);
            }
        }
    }

    private Properties createDefaults() {
        final Properties properties = new Properties();
        properties.setProperty(ACCEPTABLE_EXTENSIONS, "fit,gtx,tcx");
        properties.setProperty(NEW_DIRECTORY, DEFAULT_APPLICATION_DIRECTORY + "new");
        properties.setProperty(COMPLETE_DIRECTORY, DEFAULT_APPLICATION_DIRECTORY + "complete");
        properties.setProperty(ERROR_DIRECTORY, DEFAULT_APPLICATION_DIRECTORY + "error");
        properties.setProperty(CHECK_INTERVAL, String.valueOf(DEFAULT_CHECK_INTERVAL));
        properties.setProperty(CONNECT_TIMEOUT, String.valueOf(DEFAULT_CONNECT_TIMEOUT));
        return properties;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getUsername() {
        return getProperty(USERNAME_KEY);
    }

    @Override
    public String getPassword() {
        final Console console = System.console();
        String password = getProperty(PASSWORD_KEY);
        while (password == null && console != null) {
            password = new String(console.readPassword("Password: "));
        }
        if (password != null){
            properties.setProperty(PASSWORD_KEY, password);
        }
        return password;
    }

    @Override
    public String getAcceptableExtensions() {
        return getProperty(ACCEPTABLE_EXTENSIONS);
    }

    @Override
    public String getNewDirectory() {
        return getProperty(NEW_DIRECTORY);
    }

    @Override
    public String getCompleteDirectory() {
        return getProperty(COMPLETE_DIRECTORY);
    }

    @Override
    public String getErrorDirectory() {
        return getProperty(ERROR_DIRECTORY);
    }

    private Integer getInteger(String property, Integer defaultValue) {
        try {
            return Integer.valueOf(getProperty(property));
        } catch (Exception e) {
            logger.error("getInteger", e);
            return defaultValue;
        }
    }

    @Override
    public Integer getCheckInterval() {
        return getInteger(CHECK_INTERVAL,DEFAULT_CHECK_INTERVAL);
    }

    @Override
    public Integer getConnectTimeout() {
        return getInteger(CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
    }
}
