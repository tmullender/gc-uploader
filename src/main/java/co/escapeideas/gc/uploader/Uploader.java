package co.escapeideas.gc.uploader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * User: tmullender
 * Date: 31/03/14
 * Time: 11:11
 */
public class Uploader {
    private static final Logger logger = Logger.getLogger(Uploader.class.getName());
    private static final String UPLOAD_URL = "http://connect.garmin.com/proxy/upload-service-1.1/json/upload/.";
    private static final String FILE_ID = "data";
    private static final String RESPONSE_ID = "responseContentType";
    private static final ContentBody RESPONSE_TYPE = new StringBody("text/html", ContentType.TEXT_HTML);

    private final HttpClient httpClient;

    public Uploader(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void upload(File file) throws UploadException {
       if (file == null || !file.exists()) {
           logger.warning("Unable to upload: " + file);
       } else {
           upload(getUploadUrl(file), file);
       }
    }

    private String getUploadUrl(File file) {
        return UPLOAD_URL + getExtension(file.getName());
    }

    private void upload(String url, File file) throws UploadException {
        final HttpPost upload = new HttpPost(url);
        upload.setEntity(createEntity(file));
        try {
            final HttpResponse response = httpClient.execute(upload);
            logger.info("upload: " + response);
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            throw new UploadException("Error in HTTP execute", e);
        }
    }

    private HttpEntity createEntity(File file) {
        FileBody data = new FileBody(file);
        return MultipartEntityBuilder.create()
                .addPart(FILE_ID, data)
                .addPart(RESPONSE_ID, RESPONSE_TYPE)
                .build();
    }

    public static void main(String... args){
        final Uploader uploader = new Uploader(HttpClients.createDefault());
        final String file;
        if (args.length > 0){
            file = args[0];
        } else {
            file = "/tmp/myfile.fit";
        }
        try {
            uploader.upload(new File(file));
        } catch (UploadException e) {
            e.printStackTrace();
        }
    }
}
