package com.dle;

import com.dle.bean.database.ShopInfo;
import com.dle.bean.product.detail.ProductDetailResponse;
import com.dle.bean.product.list.ProductListRequest;
import com.dle.bean.product.list.ProductListResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Products {

    String appKey = ConfigProvider.getConfig().getValue("app.key", String.class);

    String baseUrl = ConfigProvider.getConfig().getValue("shop.open-api.base", String.class);
    String listProductPath = ConfigProvider.getConfig().getValue("shop.listProduct.path", String.class);
    String productDetailPath = ConfigProvider.getConfig().getValue("shop.productDetail.path", String.class);


    public static ProductDetailResponse getProductDetail(ShopInfo shopInfo, String productId) {
        return RequestBuilder.call(shopInfo,
                ConfigUtils.apiBaseUrl,
                ConfigUtils.productDetailPath.replace("{product_id}", productId),
                new HashMap<>(),
                null,
                ProductDetailResponse.class);
    }

    public static ProductListResponse getProductList(ShopInfo shopInfo, Map<String, String> parameters, ProductListRequest requestBody) {
        return RequestBuilder.call(shopInfo,
                ConfigUtils.apiBaseUrl,
                ConfigUtils.listProductPath,
                parameters, requestBody,
                ProductListResponse.class);
    }

    public ProductDetailResponse getProductDetail(String shopCipher, String accessToken, String productId) {
        HttpClient httpClient = HttpClients.createDefault();
        String productDetailPath1 = productDetailPath.replace("{product_id}", productId);
        try {
            long currentTimestamp = System.currentTimeMillis() / 1000L;
            Map<String, String> parameters = new HashMap<>();
            parameters.put("app_key", appKey);
            parameters.put("timestamp", String.valueOf(currentTimestamp));
            parameters.put("shop_cipher", shopCipher);

            String sign = Signature.calculate(null, parameters, productDetailPath1, null, "");
            parameters.put("sign", sign);
            // Build the URI with the query parameters
            URI uri = new URIBuilder(baseUrl + productDetailPath1)
                    .addParameters(Signature.queryParametersGenerate(parameters))
                    .build();

            // Create a GET request
            HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("x-tts-access-token", accessToken);

            // Execute the request
            HttpResponse response = httpClient.execute(request);

            // Check the status code
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Parse the response to get the shop_cipher
                ProductDetailResponse detailResponse = new ObjectMapper().readValue(response.getEntity().getContent(), ProductDetailResponse.class);
                return detailResponse;
            } else {
                // Handle the error or return null
                System.out.println("Request failed with status code: " + statusCode);
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
            return  null;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProductListResponse getListProduct(String shopCipher, String accessToken, ProductListRequest requestBody) {
        HttpClient httpClient = HttpClients.createDefault();
        long currentTimestamp = System.currentTimeMillis() / 1000L;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("app_key", appKey);
        parameters.put("timestamp", String.valueOf(currentTimestamp));
        parameters.put("page_size", "100");
        parameters.put("shop_cipher", shopCipher);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String bodyString = objectMapper.writeValueAsString(requestBody);
            String sign = Signature.calculate(null, parameters, listProductPath, null, bodyString);
            parameters.put("sign", sign);
            URI uri = new URIBuilder(baseUrl + listProductPath)
                    .addParameters(Signature.queryParametersGenerate(parameters))
                    .build();
            StringEntity requestEntity = new StringEntity(
                    bodyString,
                    ContentType.APPLICATION_JSON);
            HttpPost post = new HttpPost(uri);
            post.setHeader("x-tts-access-token", accessToken);
            post.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                ProductListResponse orderListResponse = objectMapper.readValue(response.getEntity().getContent(), ProductListResponse.class);
                return orderListResponse;
            } else {
                // Handle the error or return null
                System.out.println("Request failed with status code: " + statusCode);
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
            return null;

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
