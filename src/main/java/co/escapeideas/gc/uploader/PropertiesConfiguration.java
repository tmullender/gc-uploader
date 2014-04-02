package co.escapeideas.gc.uploader;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 01/04/14
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesConfiguration implements Configuration {

    private static final Logger logger = Logger.getLogger(PropertiesConfiguration.class.getName());
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String ACCEPTABLE_EXTENSIONS = "acceptable.extensions";
    private static final String NEW_DIRECTORY = "upload.new.directory";

    private final Properties properties;

    public PropertiesConfiguration() {
        properties = createDefaults();
    }

    public PropertiesConfiguration(File file) {
        this();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            logger.throwing(PropertiesConfiguration.class.getName(), "init", e);
        }
    }

    private Properties createDefaults() {
        final Properties properties = new Properties();
        properties.setProperty(ACCEPTABLE_EXTENSIONS, "fit,gtx,tcx");
        properties.setProperty(NEW_DIRECTORY, System.getProperty("user.home") + "/.gc-uploader/new");
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
        properties.setProperty(PASSWORD_KEY, password);
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
}
