package co.escapeideas.gc.uploader;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 31/03/14
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class UploadException extends Exception {
    public UploadException(String message, Exception exception) {
        super(message, exception);
    }
}
