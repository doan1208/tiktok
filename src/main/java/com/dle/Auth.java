package com.dle;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.microprofile.config.ConfigProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.http.HttpRequest;
import java.util.concurrent.CompletionStage;

public class Auth {

    static String getTokenUrl = ConfigProvider.getConfig().getValue("shop.auth.getToken", String.class);

    static String refreshTokenUrl = ConfigProvider.getConfig().getValue("shop.auth.refreshToken", String.class);

    static String baseUrl = ConfigProvider.getConfig().getValue("shop.open-api.base", String.class);

    static String authorizationPath = ConfigProvider.getConfig().getValue("shop.authorization.path", String.class);



    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .executor(executorService)
            .version(java.net.http.HttpClient.Version.HTTP_2)
            .build();


    CompletionStage<String> get(String appKey, String appSecret, String authCode, boolean isRefresh) throws URISyntaxException {
        URI uri = new URIBuilder(isRefresh ? refreshTokenUrl : getTokenUrl)
                .addParameter("app_key", appKey)
                .addParameter("app_secret", appSecret)
                .addParameter(isRefresh ? "refresh_token" : "auth_code", authCode)
                .addParameter("grant_type", isRefresh ? "refresh_token" : "authorized_code")
                .build();
        return this.httpClient
                .sendAsync(
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(uri)
                                .header("Accept", "application/json")
                                .build()
                        ,
                        java.net.http.HttpResponse.BodyHandlers.ofString()
                )
                .thenApply(java.net.http.HttpResponse::body)
                .thenApply(stringHttpResponse -> stringHttpResponse)
                .toCompletableFuture();
    }
    public HttpUriRequest getShopCipher(String appKey, String secret, String accessToken) {
        try {
            long currentTimestamp = System.currentTimeMillis() / 1000L;
            Map<String, String> parameters = new HashMap<>();
            parameters.put("app_key", appKey);
            parameters.put("timestamp", String.valueOf(currentTimestamp));
            String sign = Signature.calculate(secret, parameters, authorizationPath, null, "");

            // Build the URI with the query parameters
            URI uri = new URIBuilder(baseUrl + authorizationPath)
                    .addParameter("app_key", appKey)
                    .addParameter("timestamp", String.valueOf(currentTimestamp))
                    .addParameter("sign", sign)
                    .build();

            // Create a GET request
            HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("x-tts-access-token", accessToken);

            return request;

//            return httpClient.execute(request);

            // Execute the request
//            HttpResponse response = httpClient.execute(request);

            // Check the status code
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == 200) {
//                // Parse the response to get the shop_cipher
//                String responseBody = EntityUtils.toString(response.getEntity());
//                return responseBody;
//            } else {
//                // Handle the error or return null
//                System.out.println("Request failed with status code: " + statusCode);
//                return null;
//            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpUriRequest getOrRefreshToken(String appKey, String appSecret, String authCode, boolean isRefresh) {
        try {
            // Build the URI with the query parameters
            URI uri = new URIBuilder(isRefresh ? refreshTokenUrl : getTokenUrl)
                    .addParameter("app_key", appKey)
                    .addParameter("app_secret", appSecret)
                    .addParameter(isRefresh ? "refresh_token" : "auth_code", authCode)
                    .addParameter("grant_type", isRefresh ? "refresh_token" : "authorized_code")
                    .build();
            return new HttpGet(uri);

            // Create a GET request
//            HttpGet request = new HttpGet(uri);

//            return httpClient.execute(request);

            // Execute the request
//            HttpResponse response = httpClient.execute(request);
//
//            // Check the status code
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == 200) {
//                // Parse the JSON response
//                String responseBody = EntityUtils.toString(response.getEntity());
//                return responseBody;
//            } else {
//                // Handle the error or return null
//                System.out.println("Request failed with status code: " + statusCode);
//                return null;
//            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }



}
