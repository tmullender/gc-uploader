package co.escapeideas.gc.uploader;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * User: tmullender
 * Date: 31/03/14
 * Time: 11:11
 */
public class Uploader {
    private static final Logger logger = LoggerFactory.getLogger(Uploader.class);

    private static final String UPLOAD_URL = "http://connect.garmin.com/proxy/upload-service-1.1/json/upload/.";
    private static final String FILE_ID = "data";
    private static final String RESPONSE_ID = "responseContentType";
    private static final ContentBody RESPONSE_TYPE = new StringBody("text/html", ContentType.TEXT_HTML);

    private static final Pattern SUCCESS = Pattern.compile(".*\"code\":202,.*|.*\"failures\":\\[\\].*");

    private final Configuration configuration;
    private final HttpClient httpClient;

    public Uploader(Configuration configuration, HttpClient httpClient) {
        this.configuration = configuration;
        this.httpClient = httpClient;
    }

    public void upload(File file) throws UploadException {
       if (file == null || !file.exists()) {
           logger.warn("Unable to upload: {}", file);
       } else {
           logger.debug("Uploading: {}", file);
           final String uploadUrl = getUploadUrl(file);
           final String response = upload(uploadUrl, file);
           handleResponse(file, response);
       }
    }

    private void handleResponse(File file, String response) throws UploadException {
        logger.debug("Handling response: {}", response);
        if (SUCCESS.matcher(response).matches()){
            move(configuration.getCompleteDirectory(), file);
        } else {
            move(configuration.getErrorDirectory(), file);
        }
    }

    private void move(String directory, File file) throws UploadException {
        File newFile = new File(directory, file.getName());
        if (newFile.exists()){
            newFile = new File(directory, UUID.randomUUID().toString());
        }
        logger.debug("Moving file to: {}", newFile.getAbsolutePath());
        try {
            FileUtils.moveFile(file, newFile);
        } catch (IOException e) {
            throw new UploadException("Error moving complete file", e);
        }
    }

    private String getUploadUrl(File file) {
        return UPLOAD_URL + getExtension(file.getName());
    }

    private String upload(String url, File file) throws UploadException {
        logger.debug("post {} with {}", url, file);
        final HttpPost upload = new HttpPost(url);
        upload.setEntity(createEntity(file));
        try {
            final HttpResponse response = httpClient.execute(upload);
            logger.info("response: {}", response);
            return EntityUtils.toString(response.getEntity());
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

}
