package co.escapeideas.gc.uploader;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: tmullender
 * Date: 31/03/14
 * Time: 23:32
 * To change this template use File | Settings | File Templates.
 */
public class Login {

    private static final Logger logger = LoggerFactory.getLogger(Login.class);

    private static final String SSO_PAGE = "https://sso.garmin.com/sso/login";
    private static final String POST_AUTH_PAGE = "http://connect.garmin.com/post-auth/login";

    private static final Pattern LT_VALUE = Pattern.compile(".*name=[\"']?lt[\"']?\\s+value=[\"']?(\\w*)[\"']?.*");
    private static final Pattern TICKET_VALUE = Pattern.compile(".*ticket=([^']+)'");

    private final HttpClient httpClient;

    public Login(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void login(String username, String password) throws LoginException {
        if (StringUtils.isEmpty(username)){
            logger.warn("Username is empty");
        } else {
            final String ssoPage = getSSOPage();
            final String loginPage = postSSOUsernameAndPassword(username, password, getLT(ssoPage));
            final Header location = getPostAuth(getTicket(loginPage));
            if (location != null){
                getPostAuthRedirect(location.getValue());
            }
        }
    }

    private String getSSOPage() throws LoginException {
        return getPage(SSO_PAGE, getSSOParameters());
    }

    private Header getPostAuth(String ticket) throws LoginException {
        final Map<String, String> params = new HashMap<String, String>(1);
        params.put("ticket", ticket);
        final HttpResponse httpResponse = get(POST_AUTH_PAGE, params);
        return httpResponse.getFirstHeader("location");
    }

    private void getPostAuthRedirect(String location) throws LoginException {
        get(location, Collections.<String, String>emptyMap());
    }

    private String getTicket(String loginPage) {
        final Matcher matcher = TICKET_VALUE.matcher(loginPage);
        logger.debug("Getting ticket from: {}", loginPage);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String getLT(String ssoPage) {
        final Matcher matcher = LT_VALUE.matcher(ssoPage);
        logger.debug("Getting lt from: {}", ssoPage);
        if (matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    private Map<String, String> getSSOParameters() {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("service", POST_AUTH_PAGE);
        params.put("clientId", "GarminConnect");
        params.put("consumeServiceTicket", "false");
        return params;
    }

    private String getPage(String url, Map<String, String> params) throws LoginException {
        try {
            final HttpResponse httpResponse = get(url, params);
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            throw new LoginException("Error converting page to string", e);
        }
    }

    private HttpResponse get(String url, Map<String, String> params) throws LoginException {
        logger.debug("get {} with {}", url, params);
        try {
            final RequestBuilder requestBuilder = RequestBuilder.get().setUri(url);
            for (Map.Entry<String, String> entry : params.entrySet()){
                requestBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            final HttpUriRequest request = requestBuilder.build();
            final HttpResponse response = httpClient.execute(request);
            logger.info("get response {}", response);
            return response;
        } catch (IOException e) {
            throw new LoginException("Error getting homepage", e);
        }
    }

    private String postSSOUsernameAndPassword(String username, String password, String lt) throws LoginException {
        final Map<String, String> params = new HashMap<String, String>();
        params.putAll(getSSOParameters());
        params.put("username", username);
        params.put("password", password);
        params.put("_eventId", "submit");
        params.put("embed", "true");
        params.put("lt", lt);
        return postPage(SSO_PAGE, params);
    }

    private String postPage(String url, Map<String, String> params) throws LoginException {
        logger.debug("post {} with {}", url, params);
        try {
            final RequestBuilder requestBuilder = RequestBuilder.post().setUri(url);
            for (Map.Entry<String, String> entry : params.entrySet()){
                requestBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            final HttpUriRequest request = requestBuilder.build();
            final HttpResponse response = httpClient.execute(request);
            logger.info("post response {}",  response);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new LoginException("Error getting homepage", e);
        }
    }

}
