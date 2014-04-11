package co.escapeideas.gc.uploader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 11/04/14
 * Time: 23:07
 */
public class HTTPClient {

    private final HttpClient httpClient;
    private final BasicCookieStore cookieStore;

    public HTTPClient(Configuration configuration) {
        final RequestConfig config = RequestConfig.custom()
                .setCircularRedirectsAllowed(true)
                .setConnectionRequestTimeout(configuration.getConnectTimeout())
                .setConnectTimeout(configuration.getConnectTimeout())
                .setSocketTimeout(configuration.getConnectTimeout())
                .setStaleConnectionCheckEnabled(true)
                .build();
        cookieStore = new BasicCookieStore();
        this.httpClient =  HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    public HttpResponse execute(HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }

    public void clearCookies() {
        cookieStore.clear();
    }
}
