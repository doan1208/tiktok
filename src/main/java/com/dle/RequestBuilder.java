package com.dle;

import com.dle.bean.BaseResponse;
import com.dle.bean.database.ShopInfo;
import com.dle.bean.shipping.ShippingDocumentResponse;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class RequestBuilder<T extends BaseResponse<D>, D> {
    static ObjectMapper parser = new ObjectMapper();

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    Class<T> hits;

    public RequestBuilder(Class<T> typeArgumentClass) {
        this.hits = typeArgumentClass;
    }

    public T call(ShopInfo shopInfo, String domain, String path, Map<String, String> parameters, Object body) {
        parameters.put("app_key", shopInfo.getAppKey());
        parameters.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        String bodyStr = "";
        try {
            bodyStr = parser.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String sign = Signature.calculate(shopInfo.getAppSecret(), parameters, path, null, bodyStr);
        parameters.put("sign", sign);
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = new URIBuilder(domain + path).addParameters(Signature.queryParametersGenerate(parameters)).build();
            HttpUriRequest rq;
            if (bodyStr != null && !bodyStr.isBlank()) {
                rq = new HttpPost(uri);
                StringEntity requestEntity = new StringEntity(
                        bodyStr,
                        ContentType.APPLICATION_JSON);
                ((HttpPost)rq).setEntity(requestEntity);
            } else {
                rq = new HttpGet(uri);
            }
            rq.addHeader("Content-Type", "application/json");
            rq.addHeader("x-tts-access-token", parameters.get("access_token"));
            HttpResponse response = httpClient.execute(rq);

            // Check the status code
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return parser.readValue(response.getEntity().getContent(), hits);
            } else {
                // Handle the error or return null
                System.err.println("Request failed with status code: " + statusCode);
//                System.out.println(EntityUtils.toString(response.getEntity()));

                BaseResponse baseResponse = parser.readValue(response.getEntity().getContent(), BaseResponse.class);

//                T t = hits.newInstance();
//                t.setCode(baseResponse.getCode());
//                t.setMessage(baseResponse.getMessage());
//                t.setRequestId(baseResponse.getRequestId());
//                return t;

                return (T) baseResponse.copyToChild(hits.newInstance());
            }

        } catch (URISyntaxException | IOException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <U extends BaseResponse> U call(ShopInfo shopInfo, String domain, String path, Map<String, String> parameters, Object body, Class<U> uClass) {
        // Common parameters
        parameters.put("app_key", shopInfo.getAppKey());
        parameters.put("shop_cipher", shopInfo.getCipher());
        parameters.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        String bodyStr = "";
        try {
            bodyStr = parser.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String sign = Signature.calculate(shopInfo.getAppSecret(), parameters, path, null, "null".equals(bodyStr) ? "" : bodyStr);
        parameters.put("sign", sign);
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = new URIBuilder(domain + path).addParameters(Signature.queryParametersGenerate(parameters)).build();
            HttpUriRequest rq;
            if (!"null".equals(bodyStr) && bodyStr != null && !bodyStr.isBlank()) {
                rq = new HttpPost(uri);
                StringEntity requestEntity = new StringEntity(
                        bodyStr,
                        ContentType.APPLICATION_JSON);
                ((HttpPost)rq).setEntity(requestEntity);
            } else {
                rq = new HttpGet(uri);
            }
            rq.addHeader("Content-Type", "application/json");
            rq.addHeader("x-tts-access-token", shopInfo.getAccessToken());
            HttpResponse response = httpClient.execute(rq);

            // Check the status code
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return parser.readValue(response.getEntity().getContent(), uClass);
            } else {
                // Handle the error or return null
                BaseResponse baseResponse = parser.readValue(response.getEntity().getContent(), BaseResponse.class);
                System.err.println("Request failed with status code: " + statusCode);

                System.err.println(parser.writeValueAsString(baseResponse));

                return (U) baseResponse.copyToChild(uClass.newInstance());
            }

        } catch (URISyntaxException | IOException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <U extends BaseResponse> U call1(CloseableHttpClient httpClient, ShopInfo shopInfo, String domain, String path, Map<String, String> parameters, Object body, Class<U> uClass) {
        // Common parameters
        parameters.put("app_key", shopInfo.getAppKey());
        parameters.put("shop_cipher", shopInfo.getCipher());
        parameters.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        String bodyStr = "";
        try {
            bodyStr = parser.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String sign = Signature.calculate(shopInfo.getAppSecret(), parameters, path, null, "null".equals(bodyStr) ? "" : bodyStr);
        parameters.put("sign", sign);
        try {
            URI uri = new URIBuilder(domain + path).addParameters(Signature.queryParametersGenerate(parameters)).build();
            HttpUriRequest rq;
            if (!"null".equals(bodyStr) && bodyStr != null && !bodyStr.isBlank()) {
                rq = new HttpPost(uri);
                StringEntity requestEntity = new StringEntity(
                        bodyStr,
                        ContentType.APPLICATION_JSON);
                ((HttpPost)rq).setEntity(requestEntity);
            } else {
                rq = new HttpGet(uri);
            }
            rq.addHeader("Content-Type", "application/json");
            rq.addHeader("x-tts-access-token", shopInfo.getAccessToken());
            HttpResponse response = httpClient.execute(rq);

            // Check the status code
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return parser.readValue(response.getEntity().getContent(), uClass);
            } else {
                // Handle the error or return null
                BaseResponse baseResponse = parser.readValue(response.getEntity().getContent(), BaseResponse.class);
                System.err.println("Request failed with status code: " + statusCode);

                System.err.println(parser.writeValueAsString(baseResponse));

                return (U) baseResponse.copyToChild(uClass.newInstance());
            }

        } catch (URISyntaxException | IOException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <U extends BaseResponse> U call2(ShopInfo shopInfo, String domain, String path, Map<String, String> parameters, Object body, Class<U> uClass, CloseableHttpClient httpClient) {
        // Common parameters
        parameters.put("app_key", shopInfo.getAppKey());
        parameters.put("shop_cipher", shopInfo.getCipher());
        parameters.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        String bodyStr = "";
        try {
            bodyStr = parser.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String sign = Signature.calculate(shopInfo.getAppSecret(), parameters, path, null, "null".equals(bodyStr) ? "" : bodyStr);
        parameters.put("sign", sign);
        try {
            URI uri = new URIBuilder(domain + path).addParameters(Signature.queryParametersGenerate(parameters)).build();
            HttpUriRequest rq;
            if (!"null".equals(bodyStr) && bodyStr != null && !bodyStr.isBlank()) {
                rq = new HttpPost(uri);
                StringEntity requestEntity = new StringEntity(
                        bodyStr,
                        ContentType.APPLICATION_JSON);
                ((HttpPost)rq).setEntity(requestEntity);
            } else {
                rq = new HttpGet(uri);
            }
            rq.addHeader("Content-Type", "application/json");
            rq.addHeader("x-tts-access-token", shopInfo.getAccessToken());
            HttpResponse response = httpClient.execute(rq);

            // Check the status code
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return parser.readValue(response.getEntity().getContent(), uClass);
            } else {
                // Handle the error or return null
                BaseResponse baseResponse = parser.readValue(response.getEntity().getContent(), BaseResponse.class);
                System.err.println("Request failed with status code: " + statusCode);

                System.err.println(parser.writeValueAsString(baseResponse));

                return (U) baseResponse.copyToChild(uClass.newInstance());
            }

        } catch (URISyntaxException | IOException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpGet get(ShopInfo shopInfo, String domain, String path, Map<String, String> parameters) throws URISyntaxException {
        // Common parameters
        parameters.put("app_key", shopInfo.getAppKey());
        parameters.put("shop_cipher", shopInfo.getCipher());
        parameters.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        String sign = Signature.calculate(shopInfo.getAppSecret(), parameters, path, null, "");
        parameters.put("sign", sign);
        URI uri = new URIBuilder(domain + path).addParameters(Signature.queryParametersGenerate(parameters)).build();
        HttpGet get = new HttpGet(uri);
        get.addHeader("Content-Type", "application/json");
        get.addHeader("x-tts-access-token", shopInfo.getAccessToken());
        return get;
    }

//    public static ShippingDocumentResponse getShippingDocument(ShopInfo shopInfo, CloseableHttpClient httpClient, String path, Map<String, String> parameters) {
//        HttpGet rq = get(shopInfo, ConfigUtils.apiBaseUrl, path, parameters);
//        HttpResponse response = httpClient.execute(rq);
//
//        // Check the status code
//        int statusCode = response.getStatusLine().getStatusCode();
//        if (statusCode == 200) {
//            return parser.readValue(response.getEntity().getContent(), ShippingDocumentResponse.class);
//        } else {
//            // Handle the error or return null
//            BaseResponse baseResponse = parser.readValue(response.getEntity().getContent(), BaseResponse.class);
//            System.err.println("Request failed with status code: " + statusCode);
//
//            System.err.println(parser.writeValueAsString(baseResponse));
//
//            return (BaseResponse<ShippingDocumentResponse>) baseResponse.copyToChild(ShippingDocumentResponse.class);
//    }

}
