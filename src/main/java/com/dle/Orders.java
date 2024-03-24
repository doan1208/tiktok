package com.dle;

import com.dle.bean.BaseResponse;
import com.dle.bean.database.ShopInfo;
import com.dle.bean.order.OrderListRequest;
import com.dle.bean.order.OrderListResponse;
import com.dle.bean.order.detail.OrderDetailResponse;
import com.dle.bean.shipping.ShippingDocumentResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Orders {

    public static OrderListResponse getOrderList(ShopInfo shopInfo, Map<String, String> parameters, OrderListRequest requestBody) {
//        parameters.put("access_token", shopInfo.getAccessToken());
//        parameters.put("shop_cipher", shopInfo.getCipher());
//        parameters.put("page_size", "100");
//        return new RequestBuilder<>(OrderListResponse.class).call(baseUrl, orderListPath, parameters, requestBody);
        return RequestBuilder.call(shopInfo, ConfigUtils.apiBaseUrl, ConfigUtils.listOrderPath, parameters, requestBody, OrderListResponse.class);
    }

    public static ShippingDocumentResponse getShippingDocument(ShopInfo shopInfo, Map<String, String> parameters, String packageId) {
        return RequestBuilder.call(shopInfo, ConfigUtils.apiBaseUrl, ConfigUtils.shippingDocumentPath.replace("{package_id}", packageId), parameters, null, ShippingDocumentResponse.class);
    }

    public static ShippingDocumentResponse getShippingDocument1(ShopInfo shopInfo, Map<String, String> parameters, String packageId, CloseableHttpClient httpClient) {
        return RequestBuilder.call2(shopInfo, ConfigUtils.apiBaseUrl, ConfigUtils.shippingDocumentPath.replace("{package_id}", packageId), parameters, null, ShippingDocumentResponse.class, httpClient);
    }

    public static ShippingDocumentResponse getShippingDocument1(CloseableHttpClient httpClient, ShopInfo shopInfo, Map<String, String> parameters, String packageId) {
        String path = "/fulfillment/202309/packages/" + packageId + "/shipping_documents";
        return RequestBuilder.call1(httpClient, shopInfo, ConfigUtils.apiBaseUrl, path, parameters, null, ShippingDocumentResponse.class);
    }

    public static OrderDetailResponse getOrderDetail(ShopInfo shopInfo, Map<String, String> parameters){
        return RequestBuilder.call(shopInfo, ConfigUtils.apiBaseUrl, ConfigUtils.orderDetailPath, parameters, null, OrderDetailResponse.class);
    }

    public static HttpGet createDocumentGetRequest(ShopInfo shopInfo, Map<String, String> parameters, String packageId) {
        try {
            return RequestBuilder.get(shopInfo, ConfigUtils.apiBaseUrl, ConfigUtils.shippingDocumentPath.replace("{package_id}", packageId), parameters);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

//    public static ShippingDocumentResponse getShippingDocument2(ShopInfo shopInfo, CloseableHttpClient httpClient, String packageId) {
//        Map<String, String> documentParams = new HashMap<>();
//        documentParams.put("document_type", "SHIPPING_LABEL_AND_PACKING_SLIP");
//        documentParams.put("document_size", "A6");
//        String path = "/fulfillment/202309/packages/" + packageId + "/shipping_documents";
//        RequestBuilder.getShippingDocument(shopInfo, httpClient, path, documentParams);
//
//    }



//    public OrderListResponse getOrderList(String shopCipher, String accessToken, Map<String, String> parameters, OrderListRequest requestBody) {
//        HttpClient httpClient = HttpClients.createDefault();
//        long currentTimestamp = System.currentTimeMillis() / 1000L;
//        parameters.put("app_key", appKey);
//        parameters.put("timestamp", String.valueOf(currentTimestamp));
//        parameters.put("page_size", "100");
//        parameters.put("shop_cipher", shopCipher);
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            String bodyString = objectMapper.writeValueAsString(requestBody);
//            String sign = Signature.calculate(null, parameters, orderListPath, null, bodyString);
//            parameters.put("sign", sign);
//            URI uri = new URIBuilder(baseUrl + orderListPath)
//                    .addParameters(Signature.queryParametersGenerate(parameters))
//                    .build();
//            StringEntity requestEntity = new StringEntity(
//                    bodyString,
//                    ContentType.APPLICATION_JSON);
//            HttpPost post = new HttpPost(uri);
//            post.setHeader("x-tts-access-token", accessToken);
//            post.setEntity(requestEntity);
//
//            HttpResponse response = httpClient.execute(post);
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode == 200) {
//                OrderListResponse orderListResponse = objectMapper.readValue(response.getEntity().getContent(), OrderListResponse.class);
//                return orderListResponse;
//            } else {
//                // Handle the error or return null
//                System.out.println("Request failed with status code: " + statusCode);
//                System.out.println(EntityUtils.toString(response.getEntity()));
//            }
//            return null;
//
//        } catch (URISyntaxException | IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
