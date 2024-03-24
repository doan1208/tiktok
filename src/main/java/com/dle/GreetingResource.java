package com.dle;

import com.dle.Repository.ProductRepository;
import com.dle.Repository.SkuRepository;
import com.dle.bean.database.Sku;
import com.dle.bean.order.LineItem;
import com.dle.bean.order.Order;
import com.dle.bean.order.OrderListRequest;
import com.dle.bean.order.OrderListResponse;
import com.dle.bean.product.detail.ProductDetailResponse;
import com.dle.bean.product.list.Product;
import com.dle.bean.product.list.ProductListRequest;
import com.dle.bean.product.list.ProductListResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;
import java.util.stream.Collectors;

@Path("/hello")
public class GreetingResource {

    Auth auth = new Auth();

    @Inject
    ProductRepository productRepository;

    @Inject
    SkuRepository skuRepository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("/cipher")
//    public String hello1() {
//        return auth.getShopCipher(auth.appKey, auth.appSecret);
//    }

//    @GET
//    @Path("/token/{id}")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String hello2(@PathParam("id")@DefaultValue("2") int myParam) {
//        return auth.getOrRefreshToken(auth.appKey, auth.appSecret, 1 == myParam ? "ROW_J5IIuQAAAADUQk6vadFs40vObmHsBt1zdUjMMV2pTWNJ1uuu-vRl1mSYCHTeOd4cVkf4Nr-5KbQ" : auth.authCode, 1 == myParam);
//    }

//    @GET
//    @Path("/auth")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String hello3() {
//        return auth.getShopCipher(auth.appKey, auth.appSecret, "ROW_BmUpkgAAAADdlY0qjzTiKD68cljmsQ8qmCXPDj9JvuaQZyvxWpZnn5ktCqmhAnmcrv_l7U8mDvMVSv32ivGWpgv-AKNNH_2b3rS6Xai-TJ-QN7znFOZGkwnB70_yk15NZsOVXrJMa8cfu0F4PgX-Kn4CTAC09hliSFPmne2cP4ctGw7spqw3Mg");
//    }

//    @POST
//    @Path("/orders")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public OrderListResponse getOrderList(OrderListRequest request) {
//        return new Orders().getOrderList1("ROW_o9OzrwAAAAAlkliqZQuekPDVRPQr7iU4",
//                "ROW_c6xszQAAAADdlY0qjzTiKD68cljmsQ8qmCXPDj9JvuaQZyvxWpZnn5ktCqmhAnmcrv_l7U8mDvMVSv32ivGWpgv-AKNNH_2b3rS6Xai-TJ-QN7znFOZGk6YZcOpRO08cJCfMz_IOQiyTL5qPFQOAUbvwFxpm8E-fulp7tMtC2wzSMWS528kkEw",
//                new HashMap<>(), request);
//    }

    @POST
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ProductListResponse getListProduct(ProductListRequest request) {
        return new Products().getListProduct("ROW_o9OzrwAAAAAlkliqZQuekPDVRPQr7iU4",
                "ROW_c6xszQAAAADdlY0qjzTiKD68cljmsQ8qmCXPDj9JvuaQZyvxWpZnn5ktCqmhAnmcrv_l7U8mDvMVSv32ivGWpgv-AKNNH_2b3rS6Xai-TJ-QN7znFOZGk6YZcOpRO08cJCfMz_IOQiyTL5qPFQOAUbvwFxpm8E-fulp7tMtC2wzSMWS528kkEw",
                request);
    }

    @GET
    @Path("/products/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductDetailResponse hello2(@PathParam("id") String productId) {
        return new Products().getProductDetail("ROW_o9OzrwAAAAAlkliqZQuekPDVRPQr7iU4",
                "ROW_c6xszQAAAADdlY0qjzTiKD68cljmsQ8qmCXPDj9JvuaQZyvxWpZnn5ktCqmhAnmcrv_l7U8mDvMVSv32ivGWpgv-AKNNH_2b3rS6Xai-TJ-QN7znFOZGk6YZcOpRO08cJCfMz_IOQiyTL5qPFQOAUbvwFxpm8E-fulp7tMtC2wzSMWS528kkEw",
                productId);
    }

    @GET
    @Path("/updatedb")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String update() throws JsonProcessingException {
        skuRepository.deleteAll();
        Products products = new Products();
        ProductListResponse productListResponse = products.getListProduct("ROW_o9OzrwAAAAAlkliqZQuekPDVRPQr7iU4",
                "ROW_c6xszQAAAADdlY0qjzTiKD68cljmsQ8qmCXPDj9JvuaQZyvxWpZnn5ktCqmhAnmcrv_l7U8mDvMVSv32ivGWpgv-AKNNH_2b3rS6Xai-TJ-QN7znFOZGk6YZcOpRO08cJCfMz_IOQiyTL5qPFQOAUbvwFxpm8E-fulp7tMtC2wzSMWS528kkEw",
                new ProductListRequest());
        List<Sku> skuList = new ArrayList<>();
        for (Product product : productListResponse.getData().getProducts()) {
            ProductDetailResponse productDetailResponse = products.getProductDetail("ROW_o9OzrwAAAAAlkliqZQuekPDVRPQr7iU4",
                    "ROW_c6xszQAAAADdlY0qjzTiKD68cljmsQ8qmCXPDj9JvuaQZyvxWpZnn5ktCqmhAnmcrv_l7U8mDvMVSv32ivGWpgv-AKNNH_2b3rS6Xai-TJ-QN7znFOZGk6YZcOpRO08cJCfMz_IOQiyTL5qPFQOAUbvwFxpm8E-fulp7tMtC2wzSMWS528kkEw",
                    product.getId());
            for (com.dle.bean.product.detail.Sku sku : productDetailResponse.getData().getSkus()) {
                Sku skuBean = new Sku();
                skuBean.setSkuId(sku.getId());
                skuBean.setSalePrice(sku.getPrice().getSalePrice());
                skuBean.setTaxExclusivePrice(sku.getPrice().getTaxExclusivePrice());
                skuBean.setSize(sku.getSalesAttributes().get(0).getValueName());
                skuBean.setColor(sku.getSalesAttributes().get(1).getValueName());
                skuBean.setProductId(productDetailResponse.getData().getId());
                skuBean.setProductTitle(productDetailResponse.getData().getTitle());
                skuList.add(skuBean);
//                if (map.containsKey(sku.getId())) {
//                    System.err.println(new ObjectMapper().writeValueAsString(skuBean));
//                } else {
//                    skuRepository.persistAndFlush(skuBean);
//                    map.put(sku.getId(), skuBean);
//                }
            }
        }
        skuRepository.persist(skuList);
//        productRepository.persist(productList);


        return "OK";
    }

    @GET
    @Path("/updatedb2")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String update2() throws JsonProcessingException {
//        skuRepository.deleteAll();
        Products products = new Products();
        ProductListResponse productListResponse = products.getListProduct("ROW_tIqIcQAAAADipXw1YMWYARPlaaWDrrjE",
                "ROW_dCS0fQAAAABmO8LYpfJ1rh8Q0M-53AygL_xKF25YtvO8EUOxlNfeMY_JhxNX46b_YKyFhP84C82vhfkUXC8Saja_YZeZ6tSUvWMYHsnzjXUsyfpilDtQPsUIh1drqZO0UemMD-OEEpGHeXJGNGqhb-DBajHHqwugDqT041gJ9oF8CqBdcnk1Kg",
                new ProductListRequest());
        List<Sku> skuList = new ArrayList<>();
        for (Product product : productListResponse.getData().getProducts()) {
            ProductDetailResponse productDetailResponse = products.getProductDetail("ROW_tIqIcQAAAADipXw1YMWYARPlaaWDrrjE",
                    "ROW_dCS0fQAAAABmO8LYpfJ1rh8Q0M-53AygL_xKF25YtvO8EUOxlNfeMY_JhxNX46b_YKyFhP84C82vhfkUXC8Saja_YZeZ6tSUvWMYHsnzjXUsyfpilDtQPsUIh1drqZO0UemMD-OEEpGHeXJGNGqhb-DBajHHqwugDqT041gJ9oF8CqBdcnk1Kg",
                    product.getId());
            for (com.dle.bean.product.detail.Sku sku : productDetailResponse.getData().getSkus()) {
                Sku skuBean = new Sku();
                skuBean.setSkuId(sku.getId());
                skuBean.setSalePrice(sku.getPrice().getSalePrice());
                skuBean.setTaxExclusivePrice(sku.getPrice().getTaxExclusivePrice());
                skuBean.setSize(sku.getSalesAttributes().isEmpty() ? null : sku.getSalesAttributes().get(0).getValueName());
                skuBean.setColor(sku.getSalesAttributes().size() < 2 ? null : sku.getSalesAttributes().get(1).getValueName());
                skuBean.setProductId(productDetailResponse.getData().getId());
                skuBean.setProductTitle(productDetailResponse.getData().getTitle());
                skuList.add(skuBean);
//                if (map.containsKey(sku.getId())) {
//                    System.err.println(new ObjectMapper().writeValueAsString(skuBean));
//                } else {
//                    skuRepository.persistAndFlush(skuBean);
//                    map.put(sku.getId(), skuBean);
//                }
            }
        }
        skuRepository.persist(skuList);
//        productRepository.persist(productList);


        return "OK";
    }

    @GET
    @Path("/getdb")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sku> checkdb() {
        return skuRepository.findAll().list();
    }

//    @GET
//    @Path("/count/{status}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String count(@PathParam("status") String status) {
//        OrderListRequest orderListRequest = new OrderListRequest();
//        orderListRequest.setOrderStatus(status);
//        Map<String, String> parameters = new HashMap<>();
//        String nextPageToken = "";
//        OrderListResponse response = new Orders().getOrderList1("ROW_o9OzrwAAAAAlkliqZQuekPDVRPQr7iU4",
//                "ROW_cZKncgAAAADdlY0qjzTiKD68cljmsQ8qmCXPDj9JvuaQZyvxWpZnn5ktCqmhAnmcrv_l7U8mDvMVSv32ivGWpgv-AKNNH_2b3rS6Xai-TJ-QN7znFOZGk_e0qQ2P9yiVWjjYaq2TMVq5LR3o5SGizZ1TxROE000oY6SuXdgaxmNnXNDUBP0lQA",
//                parameters, orderListRequest);
//        nextPageToken = response.getData().getNextPageToken();
//        while (!nextPageToken.isBlank()) {
//            parameters.put("page_token", nextPageToken);
//            OrderListResponse nextPageResponse =
//                    new Orders().getOrderList1("ROW_o9OzrwAAAAAlkliqZQuekPDVRPQr7iU4",
//                            "ROW_cZKncgAAAADdlY0qjzTiKD68cljmsQ8qmCXPDj9JvuaQZyvxWpZnn5ktCqmhAnmcrv_l7U8mDvMVSv32ivGWpgv-AKNNH_2b3rS6Xai-TJ-QN7znFOZGk_e0qQ2P9yiVWjjYaq2TMVq5LR3o5SGizZ1TxROE000oY6SuXdgaxmNnXNDUBP0lQA",
//                            parameters, orderListRequest);
//            response.combine(nextPageResponse);
//            nextPageToken = nextPageResponse.getData().getNextPageToken();
//        }
//        List<LineItem> lineItems = new ArrayList<>();
//        for (Order order : response.getData().getOrders()) {
//            lineItems.addAll(order.getLineItems());
//        }
//
//        List<String> ids = lineItems.stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList());
//
//        List<Sku> skus = skuRepository.listAll();
//        Map<String, String> skuInfo = new HashMap<>();
//        for (Sku sku : skus) {
//            skuInfo.put(sku.getProductId() + "-" + sku.getSkuId(), sku.getProductTitle() + ", " + sku.getSize() + ", " + sku.getColor());
//        }
//
//        // tìm số lần xuất hiện của các phần tử
//        Map<String, Integer> count = new TreeMap<String, Integer>();
//        for (int i = 0; i < ids.size(); i++) {
//            addElement(count, ids.get(i));
//        }
//
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("Tổng số đơn hàng: ").append(response.getData().getOrders().size()).append("\n");
//        stringBuilder.append("Tổng số sản phẩm: ").append(lineItems.size()).append("\n");
//        for (String key : count.keySet()) {
//            if (!skuInfo.containsKey(key)) System.err.println(key);
//            stringBuilder.append(skuInfo.get(key) + ": " + count.get(key)).append("\n");
////            System.out.println(skuInfo.get(key) + ": " + count.get(key));
//        }
//
//        return stringBuilder.toString();
//    }
//
//    @GET
//    @Path("/count2/{status}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String count2(@PathParam("status") String status) {
//        OrderListRequest orderListRequest = new OrderListRequest();
//        orderListRequest.setOrderStatus(status);
//        Map<String, String> parameters = new HashMap<>();
//        String nextPageToken = "";
//        OrderListResponse response = new Orders().getOrderList1("ROW_tIqIcQAAAADipXw1YMWYARPlaaWDrrjE",
//                "ROW_dCS0fQAAAABmO8LYpfJ1rh8Q0M-53AygL_xKF25YtvO8EUOxlNfeMY_JhxNX46b_YKyFhP84C82vhfkUXC8Saja_YZeZ6tSUvWMYHsnzjXUsyfpilDtQPsUIh1drqZO0UemMD-OEEpGHeXJGNGqhb-DBajHHqwugDqT041gJ9oF8CqBdcnk1Kg",
//                parameters, orderListRequest);
//        nextPageToken = response.getData().getNextPageToken();
//        while (!nextPageToken.isBlank()) {
//            parameters.put("page_token", nextPageToken);
//            OrderListResponse nextPageResponse =
//                    new Orders().getOrderList1("ROW_tIqIcQAAAADipXw1YMWYARPlaaWDrrjE",
//                            "ROW_dCS0fQAAAABmO8LYpfJ1rh8Q0M-53AygL_xKF25YtvO8EUOxlNfeMY_JhxNX46b_YKyFhP84C82vhfkUXC8Saja_YZeZ6tSUvWMYHsnzjXUsyfpilDtQPsUIh1drqZO0UemMD-OEEpGHeXJGNGqhb-DBajHHqwugDqT041gJ9oF8CqBdcnk1Kg",
//                            parameters, orderListRequest);
//            response.combine(nextPageResponse);
//            nextPageToken = nextPageResponse.getData().getNextPageToken();
//        }
//        List<LineItem> lineItems = new ArrayList<>();
//        for (Order order : response.getData().getOrders()) {
//            lineItems.addAll(order.getLineItems());
//        }
//
//        List<String> ids = lineItems.stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList());
//
//        List<Sku> skus = skuRepository.listAll();
//        Map<String, String> skuInfo = new HashMap<>();
//        for (Sku sku : skus) {
//            skuInfo.put(sku.getProductId() + "-" + sku.getSkuId(), sku.getProductTitle() + ", " + sku.getSize() + ", " + sku.getColor());
//        }
//
//        // tìm số lần xuất hiện của các phần tử
//        Map<String, Integer> count = new TreeMap<String, Integer>();
//        for (int i = 0; i < ids.size(); i++) {
//            addElement(count, ids.get(i));
//        }
//
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("Tổng số đơn hàng: ").append(response.getData().getOrders().size()).append("\n");
//        stringBuilder.append("Tổng số sản phẩm: ").append(lineItems.size()).append("\n");
//        for (String key : count.keySet()) {
//            if (!skuInfo.containsKey(key)) System.err.println(key);
//            stringBuilder.append(skuInfo.get(key) + ": " + count.get(key)).append("\n");
////            System.out.println(skuInfo.get(key) + ": " + count.get(key));
//        }
//
//        return stringBuilder.toString();
//    }

    public static void addElement(Map<String, Integer> map, String element) {
        if (map.containsKey(element)) {
            int count = map.get(element) + 1;
            map.put(element, count);
        } else {
            map.put(element, 1);
        }
    }

}
