package com.dle.controller;

import com.dle.Products;
import com.dle.Repository.ShopRepository;
import com.dle.Repository.SkuRepository;
import com.dle.bean.database.ShopInfo;
import com.dle.bean.database.Sku;
import com.dle.bean.product.detail.ProductDetailResponse;
import com.dle.bean.product.detail.SalesAttribute;
import com.dle.bean.product.list.Product;
import com.dle.bean.product.list.ProductListRequest;
import com.dle.bean.product.list.ProductListResponse;
import com.dle.google.drive.GoogleDriveUtils;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Path("/product")
public class ProductController {

    @Inject
    ShopRepository shopRepository;

    @Inject
    SkuRepository skuRepository;

    @GET
    @Path("/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductDetail(@QueryParam("productId") String productId, @QueryParam("shopCode") String shopCode) {
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        return Response.ok(Products.getProductDetail(shopInfo.get(), productId)).build();
    }

    @POST
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getListProduct(ProductListRequest request, @QueryParam("shopCode") String shopCode) {
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Map<String, String> parameter = new HashMap<>();
        parameter.put("page_size", "100");
        return Response.ok(Products.getProductList(shopInfo.get(), parameter, request)).build();
    }

    @GET
    @Path("/updatedb")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(@QueryParam("shopCode") String shopCode) {
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Map<String, String> parameter = new HashMap<>();
        parameter.put("page_size", "100");
        ProductListResponse productListResponse = Products.getProductList(shopInfo.get(), parameter , new ProductListRequest());
        List<Sku> skuList = new ArrayList<>();
        for (Product product : productListResponse.getData().getProducts()) {
            ProductDetailResponse productDetailResponse = Products.getProductDetail(shopInfo.get(), product.getId());
            for (com.dle.bean.product.detail.Sku sku : productDetailResponse.getData().getSkus()) {
                Optional<Sku> skuInDb = skuRepository.find("productId = ?1 and skuId = ?2", product.getId(), sku.getId()).singleResultOptional();
                Sku skuBean = skuInDb.isEmpty() ? new Sku() : skuInDb.get();
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


        return Response.ok(skuList).build();
    }

    @GET
    @Path("/productString")
    @Produces(MediaType.TEXT_PLAIN)
    public Response productString(@QueryParam("shopCode") String shopCode) {
        StringBuilder result = new StringBuilder();
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Map<String, String> parameter = new HashMap<>();
        parameter.put("page_size", "100");
        ProductListResponse productListResponse = Products.getProductList(shopInfo.get(), parameter , new ProductListRequest());
        for (Product product : productListResponse.getData().getProducts()) {
            result.append(product.getTitle()).append("[").append(product.getId()).append("]").append("\n");
        }
        return Response.ok(result.toString()).build();
    }

    @GET
    @Path("/skuString")
    @Produces(MediaType.TEXT_PLAIN)
    public Response skuString(@QueryParam("shopCode") String shopCode, @QueryParam("productId") String productId) throws IOException, InterruptedException {
        StringBuilder result = new StringBuilder();
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Map<String, String> parameter = new HashMap<>();
        parameter.put("page_size", "100");
        ProductListResponse productListResponse = Products.getProductList(shopInfo.get(), parameter , new ProductListRequest());
//        for (Product product : productListResponse.getData().getProducts()) {
            ProductDetailResponse productDetailResponse = Products.getProductDetail(shopInfo.get(), productId);
            for (com.dle.bean.product.detail.Sku sku : productDetailResponse.getData().getSkus()) {
                result.append(sku.getSalesAttributes().stream().map(SalesAttribute::getValueName).collect(Collectors.joining(", "))).append("[").append(sku.getId()).append("]");
                result.append("\n");
            }
//        }


        Spreadsheet spreadsheet = GoogleDriveUtils.createSpreadsheet("Test");
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "1", "2", "3"
                ),
                Arrays.asList(
                        "4", "5", "6"
                ),
                Arrays.asList(
                        "7", "8", "9"
                )
                );
            GoogleDriveUtils.writeSpreadsheet(spreadsheet.getSpreadsheetId(), "SHOP", values);


        return Response.ok(result.toString()).build();
    }

    @GET
    @Path("/pushToGoogleSheets")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response pushExcelData(@QueryParam("spreadsheetName") String spreadsheetName) throws IOException, InterruptedException {
        List<ShopInfo> shopInfos = shopRepository.listAll();
        if (shopInfos.isEmpty()) {
            return Response.serverError().build();
        }
        Map<String, String> parameter = new HashMap<>();
        parameter.put("page_size", "100");
        Spreadsheet spreadsheet = GoogleDriveUtils.createSpreadsheet(spreadsheetName);
        String sheetShopName = "SHOP";
        GoogleDriveUtils.createSheet(sheetShopName, spreadsheet.getSpreadsheetId());
        List<List<Object>> sheetShopNameValues = new ArrayList<>();
        ProductListRequest productListRequest = new ProductListRequest();
        productListRequest.setStatus("ACTIVATE");
        for (ShopInfo shopInfo : shopInfos) {
            sheetShopNameValues.add(List.of(shopInfo.getName() + " [" + shopInfo.getCode() + "]"));
            ProductListResponse productListResponse = Products.getProductList(shopInfo, parameter, productListRequest);
            String sheetShopCode = shopInfo.getCode();
            GoogleDriveUtils.createSheet(sheetShopCode, spreadsheet.getSpreadsheetId());
            List<List<Object>> listSkuOfProduct = new ArrayList<>();
            for (Product product : productListResponse.getData().getProducts()) {
                listSkuOfProduct.add(List.of(product.getTitle() + " [" + product.getId() + "]"));
                ProductDetailResponse productDetailResponse = Products.getProductDetail(shopInfo, product.getId());
                String sheetShopProduct = product.getId();
                GoogleDriveUtils.createSheet(sheetShopProduct, spreadsheet.getSpreadsheetId());
                List<List<Object>> listProductOfShop = new ArrayList<>();
                for (com.dle.bean.product.detail.Sku sku : productDetailResponse.getData().getSkus()) {
                    String skuName = sku.getSalesAttributes().stream().map(SalesAttribute::getValueName).collect(Collectors.joining(", "));
                    listProductOfShop.add(List.of(skuName + " [" + sku.getId() + "]"));
                }
                GoogleDriveUtils.writeSpreadsheet(spreadsheet.getSpreadsheetId(), sheetShopProduct, listProductOfShop);
            }
            GoogleDriveUtils.writeSpreadsheet(spreadsheet.getSpreadsheetId(), sheetShopCode, listSkuOfProduct);
        }
        GoogleDriveUtils.writeSpreadsheet(spreadsheet.getSpreadsheetId(), sheetShopName, sheetShopNameValues);

        return Response.ok().build();
    }

}
