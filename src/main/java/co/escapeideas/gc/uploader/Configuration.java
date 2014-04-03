package co.escapeideas.gc.uploader;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 01/04/14
 * Time: 22:41
 * To change this template use File | Settings | File Templates.
 */
public interface Configuration {

    String getUsername();

    String getPassword();

    String getAcceptableExtensions();

    String getNewDirectory();

    String getCompleteDirectory();

    String getErrorDirectory();

    Integer getCheckInterval();
}
