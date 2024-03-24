package com.dle.controller;

import com.dle.*;
import com.dle.Repository.DocumentRepository;
import com.dle.Repository.ShopRepository;
import com.dle.Repository.SkuRepository;
import com.dle.bean.auth.AuthResponse;
import com.dle.bean.auth.ShopData;
import com.dle.bean.auth.TokenResponse;
import com.dle.bean.database.Document;
import com.dle.bean.database.ShopInfo;
import com.dle.bean.order.LineItem;
import com.dle.bean.order.Order;
import com.dle.bean.order.OrderListRequest;
import com.dle.bean.order.OrderListResponse;
import com.dle.bean.order.Package;
import com.dle.bean.order.detail.OrderDetailResponse;
import com.dle.bean.shipping.ShippingDocumentResponse;
import com.dle.google.drive.GoogleDriveUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.forms.v1.model.Answer;
import com.google.api.services.forms.v1.model.FormResponse;
import com.google.api.services.forms.v1.model.ListFormResponsesResponse;
import com.google.api.services.forms.v1.model.TextAnswer;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/shop")
public class ShopController {

    @Inject
    ShopRepository shopRepository;

    @Inject
    SkuRepository skuRepository;

    @Inject
    DocumentRepository documentRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    //Creating the Client Connection Pool Manager by instantiating the PoolingHttpClientConnectionManager class.
    static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    //Create a ClientBuilder Object by setting the connection manager
    HttpClientBuilder clientbuilder = HttpClients.custom().setConnectionManager(connManager);

    //Build the CloseableHttpClient object using the build() method.
    CloseableHttpClient httpclient = clientbuilder.build();

    static {
        //Set the maximum number of connections in the pool
        connManager.setMaxTotal(100);
        java.util.logging.Logger.getLogger(
                "org.apache").setLevel(java.util.logging.Level.SEVERE);
    }

    @POST
    @Path("/info/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public List<ShopInfo> updateInfo(ShopInfo info) throws InvocationTargetException, IllegalAccessException {
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", info.getCode()).singleResultOptional();
        if (shopInfo.isEmpty()) {
            shopRepository.persist(info);
        } else {
            ShopInfo merge = shopInfo.get();
            BeanUtils.copyProperties(merge, info);
            shopRepository.persist(merge);
        }
        return shopRepository.listAll();
    }

    @GET
    @Path("/token/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getOrRefreshToken(@QueryParam("code") String shopCode) {
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        ShopInfo info = shopInfo.get();
        HttpUriRequest request = null;
        if (info.getAccessToken() == null) {
            request = new Auth().getOrRefreshToken(info.getAppKey(), info.getAppSecret(), info.getAuthCode(), false);
        } else if ((info.getAccessTokenExpireIn() - (60 * 60 * 24)) * 1000L < System.currentTimeMillis()) {
            request = new Auth().getOrRefreshToken(info.getAppKey(), info.getAppSecret(), info.getRefreshToken(), true);
        } else return Response.ok().entity(info).build();
        HttpResponse response;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                TokenResponse tokenResponse = objectMapper.readValue(response.getEntity().getContent(), TokenResponse.class);
                if (0 != tokenResponse.getCode()) {
                    return Response.status(Response.Status.EXPECTATION_FAILED).entity(tokenResponse).build();
                }
                BeanUtils.copyProperties(info, tokenResponse.getData());
                shopRepository.persist(info);
                return Response.ok().entity(info).build();
            } else
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(response.getEntity()).build();
        } catch (IOException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

    @GET
    @Path("/cipher/update/{shopCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getOrUpdateCipher(@PathParam("shopCode") String shopCode) throws IOException, InvocationTargetException, IllegalAccessException {
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        ShopInfo info = shopInfo.get();
        HttpUriRequest request = new Auth().getShopCipher(info.getAppKey(), info.getAppSecret(), info.getAccessToken());
        HttpResponse response;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                AuthResponse authResponse = objectMapper.readValue(response.getEntity().getContent(), AuthResponse.class);
                if (0 != authResponse.getCode()) {
                    return Response.status(Response.Status.EXPECTATION_FAILED).entity(authResponse).build();
                }
                for (ShopData data : authResponse.getData().getShops()) {
                    if (info.getCode().equals(data.getCode())) {
                        BeanUtils.copyProperties(info, data);
                        shopRepository.persist(info);
                    }
                }
                return Response.ok().entity(info).build();
            } else
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(response.getEntity()).build();
        }

    }

    @GET
    @Path("/count/only")
    @Produces(MediaType.TEXT_PLAIN)
    public Response countOnly(@QueryParam("code") List<String> shopCode,
                              @QueryParam("status") String status,
                              @QueryParam("pageSize") @DefaultValue("100") String pageSize,
                              @QueryParam("createTimeGe") String createTimeGe,
                              @QueryParam("createTimeLt") String createTimeLt) throws IOException, ParseException {
        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
        //Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (list.isEmpty()) {
            return Response.serverError().build();
        }

        StringBuilder stringBuilder = new StringBuilder();
//        List<Sku> skus = skuRepository.listAll();

//        String dateFolder = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String startDate = df.format(new Date());

        for (ShopInfo info : list) {
            OrderListRequest orderListRequest = new OrderListRequest();
            if (status != null) orderListRequest.setOrderStatus(status);
            if (createTimeGe != null) {
                orderListRequest.setCreateTimeGe(df.parse(createTimeGe).getTime() / 1000);
            }
            if (createTimeLt != null) {
                orderListRequest.setCreateTimeLt(df.parse(createTimeLt).getTime() / 1000);
            }
            Map<String, String> parameters = new HashMap<>();
            parameters.put("page_size", pageSize);
            String nextPageToken = "";
            OrderListResponse response = Orders.getOrderList(info, parameters, orderListRequest);
            if (response.getCode() != 0) return Response.serverError().entity(response).build();
            nextPageToken = response.getData().getNextPageToken();
            while (!nextPageToken.isBlank()) {
                parameters.put("page_token", nextPageToken);
                OrderListResponse nextPageResponse = Orders.getOrderList(info, parameters, orderListRequest);
                int code = nextPageResponse.getCode();
                if (0 == code) {
                    response.combine(nextPageResponse);
                    nextPageToken = nextPageResponse.getData().getNextPageToken();
                } else {
                    System.err.println("Lỗi khi thực hiện call với next_page_token: " + nextPageToken);
                    try {
                        System.err.println(objectMapper.writeValueAsString(nextPageResponse));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    nextPageToken = "";
                }
            }
            List<LineItem> lineItems = new ArrayList<>();
            Map<String, List<Order>> groupPackageByItem = new HashMap<>();
            Map<String, String> skuInfo = new HashMap<>(); // Map chứa thông tin về từng sản phẩm trong tất cả đơn hàng, key là product-skuId, value là tên, sku name
            List<String> ids = new ArrayList<>(); // List chứa danh sách product-skuId để đếm xem có bn sp, và mỗi sp xuất hiện bn lần
            int cancelOrder = 0;
            int cancelItem = 0;
            for (Order order : response.getData().getOrders()) {
                for (LineItem item : order.getLineItems()) {
                    skuInfo.putIfAbsent(item.getProductId() + "-" + item.getSkuId(), item.getProductName() + ", " + item.getSkuName());
                }
                if (order.getCancellationInitiator() == null && order.getCancelReason() == null) {
                    lineItems.addAll(order.getLineItems());
                    ids.addAll(order.getLineItems().stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList()));
                    String ident = null;
                    if (order.getLineItems().size() == 1) {
                        LineItem item = order.getLineItems().get(0);
                        ident = item.getProductId() + "-" + item.getSkuId();
                    } else {
                        ident = "donCoNhieuHon1SP";
                    }
                    if (groupPackageByItem.containsKey(ident)) {
                        List<Order> val = groupPackageByItem.get(ident);
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    } else {
                        List<Order> val = new ArrayList<>();
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    }
                } else {
                    cancelOrder += 1;
                    cancelItem += order.getLineItems().size();
                    System.err.println("Đơn hàng bị cancel: " + objectMapper.writeValueAsString(order));
                }
            }
            // tìm số lần xuất hiện của các phần tử
            Map<String, Integer> count = new TreeMap<String, Integer>();
            for (int j = 0; j < ids.size(); j++) {
                addElement(count, ids.get(j));
            }

            stringBuilder.append("Shop name: ").append(info.getName()).append("\n");
            stringBuilder.append("Thời gian tạo: ").append(startDate).append("\n");
            stringBuilder.append("Số đơn hàng thành công: ").append(response.getData().getOrders().size() - cancelOrder).append("(").append(lineItems.size()).append(" sản phẩm)").append("\n");
            stringBuilder.append("Số đơn hàng bị cancel: ").append(cancelItem).append("(").append(cancelItem).append(" sản phẩm)").append("\n");
            stringBuilder.append("====================================>>Số lượng đơn theo mỗi loại<<====================================").append("\n");
            for (String key : groupPackageByItem.keySet()) {
                String name = skuInfo.get(key) == null ? key : skuInfo.get(key);
                stringBuilder.append("donCoNhieuHon1SP".equals(key) ? "Đơn có nhiều sản phẩm" : name).append(": ").append(groupPackageByItem.get(key).size()).append("\n");
            }
            stringBuilder.append("====================================>>Số lượng cụ thể từng sku<<====================================").append("\n");
            for (String key : count.keySet()) {
                if (!skuInfo.containsKey(key)) {
                    stringBuilder.append("Không có key: ").append(key).append(" trong danh sách sku!\n");
                }
                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append("[").append(key.trim()).append("]: ").append(count.get(key)).append("\n");
//                List<Order> orderBySku = groupPackageByItem.get(key);
//                int orderBySkuSize = orderBySku == null ? count.get(key) : orderBySku.size();
//                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append(": ").append(count.get(key))
//                        .append("(huỷ: ").append(count.get(key) - orderBySkuSize).append(", còn lại: ").append(orderBySkuSize).append(")\n");
            }

            stringBuilder.append("====================================>>!<<====================================").append("\n");

        }

        return Response.ok(stringBuilder.toString()).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response count(@QueryParam("code") List<String> shopCode,
                          @QueryParam("status") @DefaultValue("AWAITING_COLLECTION") String status,
                          @QueryParam("countLE") @DefaultValue("0") Integer countLE,
                          @QueryParam("pageSize") @DefaultValue("100") String pageSize,
                          @QueryParam("productSkuIds") List<String> productSkuIds) throws IOException {

        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
        //Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (list.isEmpty()) {
            return Response.serverError().build();
        }

        StringBuilder stringBuilder = new StringBuilder();
//        List<Sku> skus = skuRepository.listAll();

//        String dateFolder = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String startDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        for (ShopInfo info : list) {
            String resultFolderPath = "/Users/dle/IdeaProjects/tiktok/result/" + info.getName() + "/" + startDate + "/";
            OrderListRequest orderListRequest = new OrderListRequest();
            orderListRequest.setOrderStatus(status);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("page_size", pageSize);
            String nextPageToken = "";
            OrderListResponse response = Orders.getOrderList(info, parameters, orderListRequest);
            if (response.getCode() != 0) return Response.serverError().entity(response).build();
            nextPageToken = response.getData().getNextPageToken();
            while (!nextPageToken.isBlank()) {
                parameters.put("page_token", nextPageToken);
                OrderListResponse nextPageResponse = Orders.getOrderList(info, parameters, orderListRequest);
                int code = nextPageResponse.getCode();
                if (0 == code) {
                    response.combine(nextPageResponse);
                    nextPageToken = nextPageResponse.getData().getNextPageToken();
                } else {
                    System.err.println("Lỗi khi thực hiện call với next_page_token: " + nextPageToken);
                    try {
                        System.err.println(objectMapper.writeValueAsString(nextPageResponse));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    nextPageToken = "";
                }
            }
            List<LineItem> lineItems = new ArrayList<>();
            Map<String, List<Order>> groupPackageByItem = new HashMap<>();
            Map<String, String> skuInfo = new HashMap<>(); // Map chứa thông tin về từng sản phẩm trong tất cả đơn hàng, key là product-skuId, value là tên, sku name
            List<String> ids = new ArrayList<>(); // List chứa danh sách product-skuId để đếm xem có bn sp, và mỗi sp xuất hiện bn lần
            int cancelOrder = 0;
            int cancelItem = 0;

            forAllOrder:
            for (Order order : response.getData().getOrders()) {
                for (LineItem item : order.getLineItems()) {
                    //if (new ArrayList<String>().contains(item.getProductId())) break forAllOrder;
                    skuInfo.putIfAbsent(item.getProductId() + "-" + item.getSkuId(), item.getProductName() + ", " + item.getSkuName());
                }
                if (order.getCancellationInitiator() == null && order.getCancelReason() == null) {
                    lineItems.addAll(order.getLineItems());
                    ids.addAll(order.getLineItems().stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList()));
                    String ident = null;
                    if (order.getLineItems().size() == 1) {
                        LineItem item = order.getLineItems().get(0);
                        ident = item.getProductId() + "-" + item.getSkuId();
                    } else {
                        ident = "donCoNhieuHon1SP";
                    }
                    if (groupPackageByItem.containsKey(ident)) {
                        List<Order> val = groupPackageByItem.get(ident);
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    } else {
                        List<Order> val = new ArrayList<>();
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    }
                } else {
                    cancelOrder += 1;
                    cancelItem += order.getLineItems().size();
//                    System.err.println("Đơn hàng bị cancel: " + objectMapper.writeValueAsString(order));
                    String lineItem = order.getLineItems().stream().map(e -> e.getProductName() + "(" + e.getSkuName() + ")").collect(Collectors.joining(","));
                    System.err.println(String.format("Đơn hàng %s; gồm các sp: %s; tạo ngày '%s'; bị huỷ với lí do '%s'; bởi '%s'; cập nhật lúc: %s",
                            order.getId(), lineItem, new Date(order.getCreateTime() * 1000), order.getCancelReason(), order.getCancellationInitiator(), new Date(order.getUpdateTime() * 1000)));
                }
            }

            Map<String, String> documentParams = new HashMap<>();
            documentParams.put("document_type", "SHIPPING_LABEL_AND_PACKING_SLIP");
            documentParams.put("document_size", "A6");
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Di chuyển các folder của các ngày trước đó (giữ 3 ngày gần nhất) vào thư mục Archived
            LocalDateTime now = LocalDateTime.now();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String nowDateFolder = dateFormat.format(java.sql.Timestamp.valueOf(now));
            String nowDateFolder_1 = dateFormat.format(java.sql.Timestamp.valueOf(now.minusDays(1)));
            String nowDateFolder_2 = dateFormat.format(java.sql.Timestamp.valueOf(now.minusDays(2)));
            List<com.google.api.services.drive.model.File> googleShopFolders = GoogleDriveUtils.getGoogleSubFolders(getFolderIdByShop(info.getCode()));
            String archivedFolderId = getArchiveFolderIdByShop(info.getCode());
            for (com.google.api.services.drive.model.File folder : googleShopFolders) {
                if (archivedFolderId.equals(folder.getId())) continue;
                if (!folder.getName().startsWith(nowDateFolder) && !folder.getName().startsWith(nowDateFolder_1) && !folder.getName().startsWith(nowDateFolder_2)) {
                    List<String> moveToParentId = GoogleDriveUtils.moveFileToFolder(folder.getId(), archivedFolderId);
                    System.out.println("Move Folder ID: " + folder.getId() + " --- Name: " + folder.getName() + " To Folder: Archived" + moveToParentId);
                }
            }

            com.google.api.services.drive.model.File shopFolder = GoogleDriveUtils.createGoogleFolder(getFolderIdByShop(info.getCode()), startDate);

            // Create Folder chứa kết quả của request, bao gồm pdf, response, order success list
            Files.createDirectories(Paths.get(resultFolderPath));

            List<String> orderSuccess = new ArrayList<>();

            for (String keyProductId_SkuId : groupPackageByItem.keySet()) {
                if (!productSkuIds.isEmpty() && !productSkuIds.contains(keyProductId_SkuId)) {
                    System.out.printf("Đơn hàng chứa sp: %s[%s] sẽ không được in do sp cần in là: %s \n", skuInfo.get(keyProductId_SkuId), keyProductId_SkuId, productSkuIds);
                    continue;
                }
                PDFMergerUtility ut = new PDFMergerUtility();
                List<Order> orders = groupPackageByItem.get(keyProductId_SkuId);
                int countPdfPage = 0;
                int printedCount = 0;
                List<Document> documentList = new ArrayList<>();
                for (Order order : orders) {
                    Set<String> packageIds = order.getPackages().stream().map(Package::getId).collect(Collectors.toSet());
                    for (String packageId : packageIds) {
                        String combineOrderPackageId = order.getId() + "-" + packageId;
                        Optional<Document> documentOptional = documentRepository.find("shopCode = ?1 and packageId = ?2", info.getCode(), combineOrderPackageId).singleResultOptional();
                        if (documentOptional.isEmpty() || documentOptional.get().getCount() <= countLE) {
                            Document document = documentOptional.isEmpty() ? new Document(combineOrderPackageId, info.getCode(), new Date()) : documentOptional.get();
                            ShippingDocumentResponse documentResponse = Orders.getShippingDocument(info, documentParams, packageId);
                            int tryCount = 3;
                            while (tryCount != 0 && documentResponse.getCode() != 0) {
                                System.err.println(String.format("Tạo document lần %d có packageId là %s thất bại với lí do: %s, thử lại sau 1s", (3 - tryCount) + 1, combineOrderPackageId, objectMapper.writeValueAsString(documentResponse)));
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    System.err.println("Lỗi thread sleep 1s: " + e.getMessage());
                                }
                                documentResponse = Orders.getShippingDocument(info, documentParams, packageId);
//                            documentResponse = Orders.getShippingDocument1(httpClient, info, documentParams, packageId);
                                tryCount--;
                            }
                            if (documentResponse.getCode() == 0) {
                                if (tryCount < 3)
                                    System.err.println("Thành công sau " + (3 - tryCount) + " lần thử lại!");
                                document.setDocUrl(documentResponse.getData().getDocUrl());
                                HttpGet httpget = new HttpGet(documentResponse.getData().getDocUrl());
                                try {
                                    HttpResponse res = null;
                                    try {
                                        res = httpClient.execute(httpget);
                                    } catch (IOException e) {
                                        System.err.print("Tải PDF thất bại cho order: " + combineOrderPackageId + ", sẽ thử lại lần nữa sau 1s! ");
                                        System.err.println("Chi tiết lỗi: " + e.getMessage());
                                        Thread.sleep(1000);
                                        res = httpClient.execute(httpget);
                                    }
                                    HttpEntity entity = res.getEntity();
                                    if (entity != null) {
//                                InputStream inputStream = entity.getContent();
                                        File file = new File("/Users/dle/IdeaProjects/tiktok/temp/" + combineOrderPackageId + ".pdf");
//                                inputStream.transferTo(new FileOutputStream(file, false));
//                                Files.copy(inputStream, Paths.get(filePathString), StandardCopyOption.REPLACE_EXISTING);

                                        byte[] inputPdf = entity.getContent().readAllBytes();
                                        //byte[] inputPdfToTry = inputPdf.clone();
                                        try {
                                            writeMarkup2(inputPdf, file); //Thêm dòng: quay video khi bóc hàng vào cuối file in
                                            ut.addSource(file);
                                            orderSuccess.add(order.getId());
                                        } catch (IOException e) {
                                            System.out.printf("Thêm text vào file pdf thất bại(lí do: %s), order: %s, sẽ tải lại file và thử lại lần nữa sau 1s!! ", e.getMessage(), combineOrderPackageId);
                                            try {
                                                Thread.sleep(1000);
                                                res = httpClient.execute(httpget);
                                                HttpEntity entity1 = res.getEntity();
                                                if (entity1 != null) {
                                                    File tryFile = new File("/Users/dle/IdeaProjects/tiktok/temp/" + combineOrderPackageId + "_try.pdf");
                                                    byte[] inputPdfToTry = entity1.getContent().readAllBytes();
                                                    writeMarkup2(inputPdfToTry, tryFile);
                                                    ut.addSource(tryFile);
                                                    orderSuccess.add(order.getId());
                                                    System.out.println("Thêm text vào file pdf thành công sau 1 lần thử lại!");
                                                } else {
                                                    System.out.printf("Quá trình tải lại pdf thất bại vì entity null: %s \n", objectMapper.writeValueAsString(res.getEntity()));
                                                }
                                            } catch (IOException ioException) {
                                                System.out.printf("Quá trình tải lại pdf và thêm text thất bại với lí do: %s \n", ioException.getMessage());
                                            }

                                        }

                                        countPdfPage++;
                                        document.setCount(document.getCount() + 1);
                                        document.setDownloadDate(new Date());
                                        document.setErrorMessage(null);
                                        System.out.print("\t.");
                                    }
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                    document.setErrorMessage(e.getMessage());
                                    System.err.println("Đã tạo được document nhưng không thể tải về với lí do: " + e.getMessage());
                                }
                            } else {
//                            String errorMessage = objectMapper.writeValueAsString(documentResponse);
                                String errorMessage = String.format("Không thể tạo document có packageId là %s với lí do: %s", combineOrderPackageId, objectMapper.writeValueAsString(documentResponse));
                                document.setErrorMessage(errorMessage);
                                System.err.println(errorMessage);
                            }
                            documentList.add(document);
//                            documentRepository.persistAndFlush(document);
                        } else {
                            printedCount++;
                            System.err.println(String.format("Đơn hàng: %s, packageId: %s đã được in %d lần, sẽ bị bỏ qua !!!", order.getId(), packageId, documentOptional.get().getCount()));
                        }
                    }
                }

                // Flush document log to database!
                documentRepository.persist(documentList);

                if (countPdfPage > 0) {
                    String name = null;
                    if (printedCount != 0) {
                        name = String.format("%s(%d đơn, đã in trước đó: %d, còn lại: %d).pdf", skuInfo.get(keyProductId_SkuId) != null ? skuInfo.get(keyProductId_SkuId) : keyProductId_SkuId, orders.size(), printedCount, orders.size() - printedCount);
                    } else {
                        name = String.format("%s(%d đơn).pdf", skuInfo.get(keyProductId_SkuId) != null ? skuInfo.get(keyProductId_SkuId) : keyProductId_SkuId, orders.size());
                    }
                    ut.setDestinationFileName(resultFolderPath + name);
                    ut.mergeDocuments(null);
                    System.out.println("\n" + ut.getDestinationFileName());
                    GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "application/pdf", name, new File(ut.getDestinationFileName()));
                    System.out.println("====================================>>!<<====================================");
                }
            }

            //documentRepository.persist(documentList);
            String successOrderIdListName = "successPrintedOrderIdList(" + orderSuccess.size() + ").txt";
            java.nio.file.Path filePath = Paths.get(resultFolderPath + successOrderIdListName);
//            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            for (String str : orderSuccess) {
                Files.writeString(filePath, str + System.lineSeparator(),
                        StandardOpenOption.APPEND);
            }
            GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "text/plain", successOrderIdListName, filePath.toFile());
            System.out.println("Doneeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee!\t" + filePath.toString());
//            httpClient.close();

//            List<String> ids = lineItems.stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList());

            // tìm số lần xuất hiện của các phần tử
            Map<String, Integer> count = new TreeMap<String, Integer>();
            for (int j = 0; j < ids.size(); j++) {
                addElement(count, ids.get(j));
            }

            stringBuilder.append("Shop name: ").append(info.getName()).append("\n");
            stringBuilder.append("Thời gian tạo: ").append(startDate).append("\n");
            stringBuilder.append("Số đơn hàng thành công: ").append(response.getData().getOrders().size() - cancelOrder).append("(").append(lineItems.size()).append(" sản phẩm)").append("\n");
            stringBuilder.append("Số đơn hàng bị cancel: ").append(cancelItem).append("(").append(cancelItem).append(" sản phẩm)").append("\n");
            stringBuilder.append("====================================>>Số lượng đơn theo mỗi loại<<====================================").append("\n");
            for (String key : groupPackageByItem.keySet()) {
                String name = skuInfo.get(key) == null ? key : skuInfo.get(key);
                stringBuilder.append("donCoNhieuHon1SP".equals(key) ? "Đơn có nhiều sản phẩm" : name).append(": ").append(groupPackageByItem.get(key).size()).append("\n");
            }
            stringBuilder.append("====================================>>Số lượng cụ thể từng sku<<====================================").append("\n");
            for (String key : count.keySet()) {
                if (!skuInfo.containsKey(key)) {
                    stringBuilder.append("Không có key: ").append(key).append(" trong danh sách sku!\n");
                }
                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append(": ").append(count.get(key)).append("\n");
            }

            stringBuilder.append("====================================>>!<<====================================").append("\n\n");
            if (!productSkuIds.isEmpty()) {
                stringBuilder.append("Chỉ in các đơn hàng chứa các sản phẩm sau: \n");
                productSkuIds.forEach(e -> {
                    stringBuilder.append("\t- ").append("donCoNhieuHon1SP".equals(e) ? "Đơn có nhiều sản phẩm" : (skuInfo.get(e) == null ? e : skuInfo.get(e))).append(": ").append(groupPackageByItem.get(e).size()).append("\n");
                });
            }
            Arrays.stream(new File("/Users/dle/IdeaProjects/tiktok/temp/").listFiles()).forEach(File::delete);
//            Arrays.stream(new File("/Users/dle/IdeaProjects/tiktok/result/").listFiles()).forEach(File::delete);
            GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "text/plain", "Info.txt", stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            FileUtils.writeStringToFile(new File(resultFolderPath + "Info.txt"), stringBuilder.toString(), "UTF-8");

        }

        return Response.ok(stringBuilder.toString()).build();
    }

    public void addElement(Map<String, Integer> map, String element) {
        if (map.containsKey(element)) {
            int count = map.get(element) + 1;
            map.put(element, count);
        } else {
            map.put(element, 1);
        }
    }

//    @GET
//    @Path("/document")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response printDocument(@QueryParam("code") List<String> shopCode,
//                                  @QueryParam("documentType") @DefaultValue("SHIPPING_LABEL_AND_PACKING_SLIP") String documentType,
//                                  @QueryParam("documentSize") @DefaultValue("A6") String documentSize) {
//        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
//        if (list.isEmpty()) {
//            return Response.serverError().build();
//        }
//
//        StringBuilder stringBuilder = new StringBuilder();
//        List<Sku> skus = skuRepository.listAll();
//
//        for (ShopInfo info : list) {
//            Map<String, String> parameters = new HashMap<>();
//            parameters.put("document_type", documentType);
//            parameters.put("document_size", documentSize);
//            ShippingDocumentResponse shippingDocumentResponse = Orders.getShippingDocument(info, parameters, "");
//        }
//    }


    @GET
    @Path("/document/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentPrintHistory(@QueryParam("code") List<String> shopCode,
                                            @QueryParam("date") String dateStr) throws ParseException {
        List<ShopInfo> shopInfo = shopRepository.find("code in ?1", shopCode).list();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Date inputDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
        List<Document> documents = documentRepository.list("shopCode in ?1 and downloadDate between ?2 and ?3 order by downloadDate desc",
                shopInfo.stream().map(ShopInfo::getCode).collect(Collectors.toList()), ConfigUtils.atStartOfDay(inputDate), ConfigUtils.atEndOfDay(inputDate));
        return Response.ok(documents).build();
    }

    @DELETE
    @Path("/document/history")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response deleteDocumentPrintHistory(@QueryParam("code") List<String> shopCode,
                                               @QueryParam("date") String dateStr) throws ParseException {
        List<ShopInfo> shopInfo = shopRepository.find("code in ?1", shopCode).list();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Date inputDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
        Long documents = documentRepository.delete("shopCode in ?1 and downloadDate between ?2 and ?3",
                shopInfo.stream().map(ShopInfo::getCode).collect(Collectors.toList()), ConfigUtils.atStartOfDay(inputDate), ConfigUtils.atEndOfDay(inputDate));
        return Response.ok(documents).build();
    }

    @POST
    @Path("/document/history")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateDocumentPrintHistory(Document document, @QueryParam("code") List<String> shopCode) throws InvocationTargetException, IllegalAccessException {
        List<ShopInfo> shopInfo = shopRepository.find("code in ?1", shopCode).list();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Document documentInDb = documentRepository.findById(document.getId());
        if (documentInDb != null) {
            BeanUtils.copyProperties(documentInDb, document);
            documentRepository.persist(documentInDb);
        }
        return Response.ok(documentInDb).build();
    }

    @GET
    @Path("order/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductDetail(@QueryParam("ids") String ids, @QueryParam("code") String shopCode) {
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Map<String, String> params = new HashMap<>();
        params.put("ids", ids);
        return Response.ok(Orders.getOrderDetail(shopInfo.get(), params)).build();
    }

    @GET
    @Path("order/printById")
    public Response printDocumentByOrderId(@QueryParam("ids") String ids, @QueryParam("code") String shopCode) throws IOException {
        Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (shopInfo.isEmpty()) {
            return Response.serverError().build();
        }
        Map<String, String> params = new HashMap<>();
        params.put("ids", ids);
        OrderDetailResponse response = Orders.getOrderDetail(shopInfo.get(), params);
        PDFMergerUtility ut = new PDFMergerUtility();
        if (response.getCode() == 0 && response.getData() != null) {
            List<com.dle.bean.order.detail.Order> orders = response.getData().getOrders();
            Map<String, String> documentParams = new HashMap<>();
            documentParams.put("document_type", "SHIPPING_LABEL_AND_PACKING_SLIP");
            documentParams.put("document_size", "A6");
            int countPdfPage = 0;
            for (com.dle.bean.order.detail.Order order : orders) {
                Set<String> packageIds = order.getPackages().stream().map(com.dle.bean.order.detail.Package::getId).collect(Collectors.toSet());
                for (String packageId : packageIds) {
                    String combineOrderPackageId = order.getId() + "-" + packageId;
                    ShippingDocumentResponse documentResponse = Orders.getShippingDocument(shopInfo.get(), documentParams, packageId);
                    int tryCount = 3;
                    while (tryCount != 0 && documentResponse.getCode() != 0) {
                        System.err.println(String.format("Tạo document lần %d có packageId là %s thất bại với lí do: %s, thử lại sau 1s", (3 - tryCount) + 1, combineOrderPackageId, objectMapper.writeValueAsString(documentResponse)));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.err.println("Lỗi thread sleep 1s: " + e.getMessage());
                        }
                        documentResponse = Orders.getShippingDocument(shopInfo.get(), documentParams, packageId);
//                            documentResponse = Orders.getShippingDocument1(httpClient, info, documentParams, packageId);
                        tryCount--;
                    }
                    if (documentResponse.getCode() == 0) {
                        if (tryCount < 3) System.err.println("Thành công sau " + (3 - tryCount) + " lần thử lại!");
                        HttpGet httpget = new HttpGet(documentResponse.getData().getDocUrl());
                        try {
                            HttpResponse res = null;
                            try {
                                res = httpclient.execute(httpget);
                            } catch (IOException e) {
                                System.err.print("Tải PDF thất bại cho order: " + combineOrderPackageId + ", sẽ thử lại lần nữa sau 1s! ");
                                System.err.println("Chi tiết lỗi: " + e.getMessage());
                                Thread.sleep(1000);
                                res = httpclient.execute(httpget);
                            }
                            HttpEntity entity = res.getEntity();
                            if (entity != null) {
//                                InputStream inputStream = entity.getContent();
                                File file = new File("/Users/dle/IdeaProjects/tiktok/temp/" + combineOrderPackageId + ".pdf");
//                                inputStream.transferTo(new FileOutputStream(file, false));
//                                Files.copy(inputStream, Paths.get(filePathString), StandardCopyOption.REPLACE_EXISTING);

                                byte[] inputPdf = entity.getContent().readAllBytes();
                                //byte[] inputPdfToTry = inputPdf.clone();
                                try {
                                    writeMarkup2(inputPdf, file); //Thêm dòng: quay video khi bóc hàng vào cuối file in
                                    ut.addSource(file);
                                } catch (IOException e) {
                                    System.out.printf("Thêm text vào file pdf thất bại(lí do: %s), order: %s, sẽ tải lại file và thử lại lần nữa sau 1s!! ", e.getMessage(), combineOrderPackageId);
                                    try {
                                        Thread.sleep(1000);
                                        res = httpclient.execute(httpget);
                                        HttpEntity entity1 = res.getEntity();
                                        if (entity1 != null) {
                                            File tryFile = new File("/Users/dle/IdeaProjects/tiktok/temp/" + combineOrderPackageId + "_try.pdf");
                                            byte[] inputPdfToTry = entity1.getContent().readAllBytes();
                                            writeMarkup2(inputPdfToTry, tryFile);
                                            ut.addSource(tryFile);
                                            System.out.println("Thêm text vào file pdf thành công sau 1 lần thử lại!");
                                        } else {
                                            System.out.printf("Quá trình tải lại pdf thất bại vì entity null: %s \n", objectMapper.writeValueAsString(res.getEntity()));
                                        }
                                    } catch (IOException ioException) {
                                        System.out.printf("Quá trình tải lại pdf và thêm text thất bại với lí do: %s \n", ioException.getMessage());
                                    }

                                }

                                countPdfPage++;
                                System.out.print("\t.");
                            }
                        } catch (Exception e) {
                            System.err.println("Đã tạo được document nhưng không thể tải về với lí do: " + e.getMessage());
                        }
                    } else {
                        String errorMessage = String.format("Không thể tạo document có packageId là %s với lí do: %s", combineOrderPackageId, objectMapper.writeValueAsString(documentResponse));
                        System.err.println(errorMessage);
                    }

                }
            }
            if (countPdfPage > 0) {
                String name = "OrderResult.pdf";
                ut.setDestinationFileName("/Users/dle/IdeaProjects/tiktok/temp/" + name);
                ut.mergeDocuments(null);
                System.out.println("\n" + ut.getDestinationFileName());
                System.out.println("====================================>>!<<====================================");
            }
        }
        return Response.ok(FileUtils.readFileToByteArray(new File(ut.getDestinationFileName()))).type("application/pdf").header("Content-Disposition", "filename=\"" + "OrderResult.pdf" + "\"").build();
    }

    @POST
    @Path("order/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderList(OrderListRequest request,
                              @QueryParam("code") List<String> shopCode,
                              @QueryParam("pageSize") @DefaultValue("100") String pageSize) throws IOException {

        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
        //Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (list.isEmpty()) {
            return Response.serverError().build();
        }

        List<OrderListResponse> responses = new ArrayList<>();
        if (request.getOrderStatus() == null) request.setOrderStatus("AWAITING_COLLECTION");
        if (request.getCreateTimeGe() == null)
            request.setCreateTimeGe(ConfigUtils.atStartOfDay(new Date()).getTime() / 1000);

        for (ShopInfo info : list) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("page_size", pageSize);
            String nextPageToken = "";
            OrderListResponse response = Orders.getOrderList(info, parameters, request);
            if (response.getCode() != 0) return Response.serverError().entity(response).build();
            nextPageToken = response.getData().getNextPageToken();
            while (!nextPageToken.isBlank()) {
                parameters.put("page_token", nextPageToken);
                OrderListResponse nextPageResponse = Orders.getOrderList(info, parameters, request);
                int code = nextPageResponse.getCode();
                if (0 == code) {
                    response.combine(nextPageResponse);
                    nextPageToken = nextPageResponse.getData().getNextPageToken();
                } else {
                    System.err.println("Lỗi khi thực hiện call với next_page_token: " + nextPageToken);
                    try {
                        System.err.println(objectMapper.writeValueAsString(nextPageResponse));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    nextPageToken = "";
                }
            }

            responses.add(response);

        }
        return Response.ok(responses).build();
    }

    String getFolderIdByShop(String shopCode) {
        switch (shopCode) {
            case "VNLCW2WL9W":
                return GoogleDriveUtils.TIKTOK_BY_KEM_STUDIO_1;
            case "VNLCQBWYS6":
                return GoogleDriveUtils.TIKTOK_BY_KEM_STUDIO_2;
        }
        return GoogleDriveUtils.TIKTOK_DOCUMENT_FOLDER_ID;
    }

    String getArchiveFolderIdByShop(String shopCode) {
        switch (shopCode) {
            case "VNLCW2WL9W":
                return GoogleDriveUtils.TIKTOK_BY_KEM_STUDIO_1_ARCHIVED_FOLDER;
            case "VNLCQBWYS6":
                return GoogleDriveUtils.TIKTOK_BY_KEM_STUDIO_2_ARCHIVED_FOLDER;
        }
        return GoogleDriveUtils.TIKTOK_DOCUMENT_FOLDER_ID;
    }

    public void writeMarkup(byte[] input, File output) throws IOException {
        PDDocument pdDocument = Loader.loadPDF(input);

        //you can load new font if required, by using `ttf` file for that font
        PDFont pdfFontBold = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/Helvetica-Bold.ttf"));
        PDFont pdfFont = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/HelveticaWorld-Regular.ttf"));

        //PDPageContentStream.AppendMode.APPEND this part is must if you want just add new data in exsitnig one
        PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdDocument.getPage(0),
                PDPageContentStream.AppendMode.APPEND, true, true);

        contentStream.setFont(pdfFontBold, 20);
        //for first Line
        contentStream.beginText();
        //For adjusting location of text on page you need to adjust this two values
        contentStream.newLineAtOffset(9, 16);
        contentStream.showText("QUAY VIDEO KHI BÓC HÀNG");
        contentStream.endText();

        contentStream.setFont(pdfFont, 10);
        //for second line
        contentStream.beginText();
        contentStream.newLineAtOffset(13, 5);
        contentStream.showText("Shop chỉ giải quyết khiếu nại khi có video, Zalo: 038.898.0432");
        contentStream.endText();

//        // Vẽ hcn có góc bo tròn bao quanh chữ
//        float x = 5;
//        float y = 3;
//        float width = 500;
//        float height = 100;
//        contentStream.setLineWidth(3);
//
//        contentStream.setNonStrokingColor(Color.BLACK);
//
//        contentStream.moveTo(x, y);
//
//        // bottom of rectangle, left to right
//        contentStream.lineTo(x + width, y);
//        contentStream.curveTo(x + width + 5.9f, y + 0.14f,
//                x + width + 11.06f, y + 5.16f,
//                x + width + 10.96f, y + 10);
//
//        // right of rectangle, bottom to top
//        contentStream.lineTo(x + width + 10.96f, y + height);
//        contentStream.curveTo(x + width + 11.06f, y + height - 5.16f + 10,
//                x + width + 5.9f, y + height + 0.14f + 10,
//                x + width, y + height + 10);
//
//        // top of rectangle, right to left
//        contentStream.lineTo(x, y + height + 10);
//        contentStream.curveTo(x - 5.9f, y + height + 0.14f + 10,
//                x - 11.06f, y + height - 5.16f + 10,
//                x - 10.96f, y + height);
//
//        // left of rectangle, top to bottom
//        contentStream.lineTo(x - 10.96f, y + 10);
//        contentStream.curveTo(x - 11.06f, y + 5.16f,
//                x - 5.9f, y + 0.14f,
//                x, y);
//
//        contentStream.closePath();
//        contentStream.stroke();


        //at last you need to close the document to save data
        contentStream.close();
        //this is for saving your PDF you can save with new name
        //or you can replace existing one by giving same name
        pdDocument.save(output);
    }

    public void writeMarkup2(byte[] input, File output) throws IOException {
        PDDocument pdDocument = Loader.loadPDF(input);
        PDPage firstPage = pdDocument.getPage(0);

        //you can load new font if required, by using `ttf` file for that font
        PDFont pdfFontBold = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/Helvetica-Bold.ttf"));
        PDFont pdfFont = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/HelveticaWorld-Regular.ttf"));
        PDFont pdfFontBungeeShade = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/BungeeShade-Regular.ttf"));

        //PDPageContentStream.AppendMode.APPEND this part is must if you want just add new data in exsitnig one
        PDPageContentStream contentStream = new PDPageContentStream(pdDocument, firstPage,
                PDPageContentStream.AppendMode.PREPEND, true, true);

//        float fWidth = 238.4f / firstPage.getMediaBox().getWidth();
//        float fHeight = 336f / firstPage.getMediaBox().getHeight();
//        float factor = 0f;
//        if (fWidth > fHeight) {
//            factor = fHeight;
//        } else {
//            factor = fWidth;
//        }
//        contentStream.transform(Matrix.getScaleInstance(factor, factor));
        contentStream.transform(new Matrix(0.9f, 0.0F, 0.0F, 0.9f, 16F, 42F));


        //for first Line
//        contentStream.setFont(pdfFontBold, 13.8f);
//        contentStream.beginText();
//        //For adjusting location of text on page you need to adjust this two values
//        contentStream.newLineAtOffset(11,490);
        contentStream.setFont(pdfFontBold, 12f);
        contentStream.beginText();
        //For adjusting location of text on page you need to adjust this two values
        contentStream.newLineAtOffset(-4f, 8f);
        contentStream.showText("CHO KIỂM TRA HÀNG / QUAY VIDEO KHI BÓC HÀNG");
        contentStream.endText();

        //for second line
//        contentStream.setFont(pdfFont, 15);
//        contentStream.beginText();
//        contentStream.newLineAtOffset(35,460);
        contentStream.setFont(pdfFont, 15);
        contentStream.beginText();
        contentStream.newLineAtOffset(4f, -12f);
        contentStream.showText("Shop chỉ hỗ trợ đơn hàng khi chưa đánh giá");
        contentStream.endText();

        //for third line
//        contentStream.setFont(pdfFontBold, 16.3f);
//        contentStream.beginText();
//        contentStream.newLineAtOffset(15.5f,430);
        contentStream.setFont(pdfFontBold, 14.5f);
        contentStream.beginText();
        contentStream.newLineAtOffset(-4f, -35f);
        contentStream.showText("ZALO GIẢI QUYẾT KHIẾU NẠI: 038.898.0432");
        contentStream.endText();

        // Vẽ hcn
//        contentStream.addRect(9, 420, 355, 90);
        contentStream.addRect(-9.6f, -40f, 315, 65);
        contentStream.setLineWidth(2);
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.stroke();
        //content.setNonStrokingColor(color);
        //content.fill();


        // Vẽ logo BY KEM STUDIO
        //https://stackoverflow.com/questions/59553242/rotate-text-using-pdfbox
//        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(90), 0, 0);
//        matrix.translate(0, -firstPage.getMediaBox().getWidth());
//
//        contentStream.beginText();
//        contentStream.setTextMatrix(matrix);
//        int xPos = 305;
//        int yPos = 90;
//        int fontSize = 30;
//        float titleWidth = pdfFontBungeeShade.getStringWidth("BY K.E.M STUDIO") / 1000;
//        contentStream.newLineAtOffset(yPos - titleWidth / 2 - fontSize, firstPage.getMediaBox().getWidth() - xPos - titleWidth / 2 - fontSize);
//
//        contentStream.setFont(pdfFontBungeeShade, fontSize);
//        contentStream.showText("BY K.E.M STUDIO");
//        contentStream.endText();


        //at last you need to close the document to save data
        contentStream.close();
        //this is for saving your PDF you can save with new name
        //or you can replace existing one by giving same name
        pdDocument.save(output);
    }

    // Scale 95% và thêm thay đổi nôội dung khi có ghi chú thay đổi thông tin về kích thước, màu sắc
    public static void writeMarkup3(byte[] input, File output, FormResult formResult) throws IOException {
        PDDocument pdDocument = Loader.loadPDF(input);
        PDPage firstPage = pdDocument.getPage(0);

        //you can load new font if required, by using `ttf` file for that font
        PDFont pdfFontBold = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/Helvetica-Bold.ttf"));
        PDFont pdfFont = PDType0Font.load(pdDocument,
                new File("/Users/dle/IdeaProjects/tiktok/src/main/resources/HelveticaWorld-Regular.ttf"));

        // Đọc các dòng text và vị trí của nó trong file
        myStripper stripper = new myStripper();
        stripper.setStartPage(1); // fix it to first page just to test it
        stripper.setEndPage(1);
        stripper.getText(pdDocument);

        //PDPageContentStream.AppendMode.APPEND this part is must if you want just add new data in exsitnig one
        PDPageContentStream contentStream = new PDPageContentStream(pdDocument, firstPage,
                PDPageContentStream.AppendMode.PREPEND, true, true);

        // Nếu đơn cần thay đổi thông tin thì thêm vào thông tin cần thay đổi và kẻ 1 dường chéo
        if (formResult != null && StringUtils.isNotBlank(formResult.getChangeSkuName())) {
            float x = 0;
            float y = 0;
            for (TextLine textLine : stripper.lines) {
                List<TextPosition> positions = textLine.textPositions;
                if ((textLine.text.contains(formResult.getProductName()) && textLine.text.contains(formResult.getOriginalSkuName()))
                        || textLine.text.contains(formResult.getOriginalSkuName()) || textLine.text.contains(formResult.getOriginalSkuName().replace(" ", ""))) {
                    // Lấy vị trí cuối cùng và trước cuối cùng, kiểm tra xem nó có cách nhau xa quá k, 2 ký tự cách nhau ~2
                    // So sánh khoảng cách giữa vị trí  kết thúc của ký tự trước và vị trí bắt đầu của ký tự sau
                    if (positions.get(textLine.textPositions.size() - 2).getEndX() + 7 < positions.get(positions.size() - 1).getX()) {
                        x = positions.get(positions.size() - 3).getEndX() + 10;
                        y = positions.get(positions.size() - 3).getEndY();
                    } else {
                        x = positions.get(positions.size() - 2).getEndX() + 10;
                        y = positions.get(positions.size() - 2).getEndY();
                    }
                }
            }

            // Thêm ghi chú thay đổi size vào đúng vị trí
            contentStream.setFont(pdfFontBold, 8f);
            contentStream.beginText();
            contentStream.newLineAtOffset(x * 0.95f + 7f, y * 0.95f + 20f);
            contentStream.showText("-> " + formResult.getChangeSkuName());
            contentStream.endText();

            // Vẽ 1 đường chéo qua đơn cho dễ phân biệt
            contentStream.moveTo(firstPage.getMediaBox().getLowerLeftX(), firstPage.getMediaBox().getUpperRightY()); // 0, 420
            contentStream.lineTo(firstPage.getMediaBox().getUpperRightX(), firstPage.getMediaBox().getLowerLeftY()); // 298, 0
            contentStream.stroke();
            System.out.println(String.format("Đơn hàng: %s đã được thay đổi thông tin sp: %s, %s -> %s",
                    formResult.getOrderId(), formResult.getProductName(), formResult.getOriginalSkuName(), formResult.getChangeSkuName()));
        }

        // Thay đổi độ scale thu nhỏ, dịch nó ra giữa trang giấy
        contentStream.transform(new Matrix(0.95f, 0.0F, 0.0F, 0.95f, 7F, 20F));

        // Start-------------------
        //for first Line
        contentStream.setFont(pdfFontBold, 12f);
        contentStream.beginText();
        contentStream.newLineAtOffset(-3f, 34f);
        contentStream.showText("CHO KIỂM TRA HÀNG / QUAY VIDEO KHI BÓC HÀNG");
        contentStream.endText();

        //for second line
        contentStream.setFont(pdfFont, 15);
        contentStream.beginText();
        contentStream.newLineAtOffset(4f, 14f);
        contentStream.showText("Shop chỉ hỗ trợ đơn hàng khi chưa đánh giá");
        contentStream.endText();

        //for third line
        contentStream.setFont(pdfFontBold, 14.5f);
        contentStream.beginText();
        contentStream.newLineAtOffset(-3f, -9f);
        contentStream.showText("ZALO GIẢI QUYẾT KHIẾU NẠI: 038.898.0432");
        contentStream.endText();

        // Vẽ hcn
        contentStream.addRect(-5f, -14f, 308, 65);
        contentStream.setLineWidth(2);
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.stroke();

        //at last you need to close the document to save data
        contentStream.close();
        //this is for saving your PDF you can save with new name
        //or you can replace existing one by giving same name
        pdDocument.save(output);
    }

    @GET
    @Path("/count/only1")
    @Produces(MediaType.TEXT_PLAIN)
    public Response countOnly1(@QueryParam("code") List<String> shopCode,
                               @QueryParam("status") String status,
                               @QueryParam("pageSize") @DefaultValue("100") String pageSize,
                               @QueryParam("createTimeGe") String createTimeGe,
                               @QueryParam("createTimeLt") String createTimeLt) throws IOException, ParseException {
        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
        //Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (list.isEmpty()) {
            return Response.serverError().build();
        }

        StringBuilder stringBuilder = new StringBuilder();
//        List<Sku> skus = skuRepository.listAll();

//        String dateFolder = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String startDate = df.format(new Date());

        for (ShopInfo info : list) {
            OrderListRequest orderListRequest = new OrderListRequest();
            if (status != null) orderListRequest.setOrderStatus(status);
            if (createTimeGe != null) {
                orderListRequest.setCreateTimeGe(df.parse(createTimeGe).getTime() / 1000);
            }
            if (createTimeLt != null) {
                orderListRequest.setCreateTimeLt(df.parse(createTimeLt).getTime() / 1000);
            }
            Map<String, String> parameters = new HashMap<>();
            parameters.put("page_size", pageSize);
            String nextPageToken = "";
            OrderListResponse response = Orders.getOrderList(info, parameters, orderListRequest);
            if (response.getCode() != 0) return Response.serverError().entity(response).build();
            nextPageToken = response.getData().getNextPageToken();
            while (!nextPageToken.isBlank()) {
                parameters.put("page_token", nextPageToken);
                OrderListResponse nextPageResponse = Orders.getOrderList(info, parameters, orderListRequest);
                int code = nextPageResponse.getCode();
                if (0 == code) {
                    response.combine(nextPageResponse);
                    nextPageToken = nextPageResponse.getData().getNextPageToken();
                } else {
                    System.err.println("Lỗi khi thực hiện call với next_page_token: " + nextPageToken);
                    try {
                        System.err.println(objectMapper.writeValueAsString(nextPageResponse));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    nextPageToken = "";
                }
            }

            Map<String, FormResult> donHangDoiSizeMau = new HashMap<>();
            ListFormResponsesResponse formResponses = GoogleDriveUtils.readFormResponses("12XXqkRR7j-Fl_EvkwzDdbgCI24ufohWFIKJWa2JWnQU");
            if (!formResponses.isEmpty()) {
                for (FormResponse formRes : formResponses.getResponses()) {
                    FormResult result = new FormResult(formRes);
                    if (StringUtils.isNotBlank(result.getOrderId())) {
                        donHangDoiSizeMau.put(result.getOrderId(), result);
                    } else System.err.println("Kết quả lấy từ form về bị lỗi: " + result);
                }
            }

            List<LineItem> lineItems = new ArrayList<>();
            Map<String, List<Order>> groupPackageByItem = new HashMap<>();
            Map<String, String> skuInfo = new HashMap<>(); // Map chứa thông tin về từng sản phẩm trong tất cả đơn hàng, key là product-skuId, value là tên, sku name
            List<String> ids = new ArrayList<>(); // List chứa danh sách product-skuId để đếm xem có bn sp, và mỗi sp xuất hiện bn lần
            int cancelOrder = 0;
            int cancelItem = 0;
            int changeInformation = 0;
            for (Order order : response.getData().getOrders()) {

                FormResult changeInfo = donHangDoiSizeMau.get(order.getId());
                int countChangeItemInOrder = 0;
                for (LineItem item : order.getLineItems()) {
                    // Check xem có phải đơn hàng bị thay đổi thông tin không thì override lại thông tin thành thông tin cần thay đổi
                    if (changeInfo != null) {
                        if (item.getSkuId().equals(changeInfo.getOriginalSkuId())) {
//                            skuInfo.putIfAbsent(item.getProductId() + "-" + changeInfo.getChangeSkuId(), item.getProductName() + ", " + changeInfo.getChaneSkuName());
//                            continue;
                            item.setSkuId(changeInfo.getChangeSkuId());
                            item.setSkuName(changeInfo.getChangeSkuName());
                            changeInformation++;
                            countChangeItemInOrder++;
                        }
                    }
                    skuInfo.putIfAbsent(item.getProductId() + "-" + item.getSkuId(), item.getProductName() + ", " + item.getSkuName());
                }

                // Warning khi thấy thông tin đơn hàng cần thay đổi nhưng không thấy sku cần thay đổi trong đơn do người nhập thông tin thay đổi không chính xác
                if (changeInfo != null && countChangeItemInOrder == 0)
                    System.err.println("Thông tin thay đổi của đơn hàng không chính xác, " + changeInfo + ". Vui lòng kiểm tra lại các sku trong đơn hàng!");

                if (order.getCancellationInitiator() == null && order.getCancelReason() == null) {
                    lineItems.addAll(order.getLineItems());
                    ids.addAll(order.getLineItems().stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList()));
                    String ident = null;
                    if (order.getLineItems().size() == 1) {
                        LineItem item = order.getLineItems().get(0);
                        ident = item.getProductId() + "-" + item.getSkuId();
                    } else {
                        ident = "donCoNhieuHon1SP";
                    }
                    if (groupPackageByItem.containsKey(ident)) {
                        List<Order> val = groupPackageByItem.get(ident);
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    } else {
                        List<Order> val = new ArrayList<>();
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    }
                } else {
                    cancelOrder += 1;
                    cancelItem += order.getLineItems().size();
                    System.err.println("Đơn hàng bị cancel: " + objectMapper.writeValueAsString(order));
                }
            }
            // tìm số lần xuất hiện của các phần tử
            Map<String, Integer> count = new TreeMap<String, Integer>();
            for (int j = 0; j < ids.size(); j++) {
                addElement(count, ids.get(j));
            }

            stringBuilder.append("Shop name: ").append(info.getName()).append("\n");
            stringBuilder.append("Thời gian tạo: ").append(startDate).append("\n");
            stringBuilder.append("Số đơn hàng thành công: ").append(response.getData().getOrders().size() - cancelOrder).append("(").append(lineItems.size()).append(" sản phẩm)").append("\n");
            stringBuilder.append("Số đơn hàng bị cancel: ").append(cancelItem).append("(").append(cancelItem).append(" sản phẩm)").append("\n");
            stringBuilder.append("Số đơn hàng bị thay đổi size, màu: ").append(changeInformation).append("\n");
            stringBuilder.append("====================================>>Số lượng đơn theo mỗi loại<<====================================").append("\n");
            for (String key : groupPackageByItem.keySet()) {
                String name = skuInfo.get(key) == null ? key : skuInfo.get(key);
                stringBuilder.append("donCoNhieuHon1SP".equals(key) ? "Đơn có nhiều sản phẩm" : name).append(": ").append(groupPackageByItem.get(key).size()).append("\n");
            }
            stringBuilder.append("====================================>>Số lượng cụ thể từng sku<<====================================").append("\n");
            for (String key : count.keySet()) {
                if (!skuInfo.containsKey(key)) {
                    stringBuilder.append("Không có key: ").append(key).append(" trong danh sách sku!\n");
                }
                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append("[").append(key.trim()).append("]: ").append(count.get(key)).append("\n");
//                List<Order> orderBySku = groupPackageByItem.get(key);
//                int orderBySkuSize = orderBySku == null ? count.get(key) : orderBySku.size();
//                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append(": ").append(count.get(key))
//                        .append("(huỷ: ").append(count.get(key) - orderBySkuSize).append(", còn lại: ").append(orderBySkuSize).append(")\n");
            }

            stringBuilder.append("====================================>>!<<====================================").append("\n");

        }

        return Response.ok(stringBuilder.toString()).build();
    }


    @POST
    @Path("/count/only2")
    @Produces(MediaType.TEXT_PLAIN)
    public Response countOnly2(List<List<String>> mapping,
                               @QueryParam("code") List<String> shopCode,
                               @QueryParam("status") String status,
                               @QueryParam("pageSize") @DefaultValue("100") String pageSize,
                               @QueryParam("createTimeGe") String createTimeGe,
                               @QueryParam("createTimeLt") String createTimeLt) throws IOException, ParseException {
        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
        //Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (list.isEmpty()) {
            return Response.serverError().build();
        }

        StringBuilder stringBuilder = new StringBuilder();
//        List<Sku> skus = skuRepository.listAll();

//        String dateFolder = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String startDate = df.format(new Date());

        for (ShopInfo info : list) {
            List<Document> documents = documentRepository.list("shopCode = ?1 and downloadDate > ?2", info.getCode(), java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(5)));

            OrderListRequest orderListRequest = new OrderListRequest();
            if (status != null) orderListRequest.setOrderStatus(status);
            if (createTimeGe != null) {
                orderListRequest.setCreateTimeGe(df.parse(createTimeGe).getTime() / 1000);
            }
            if (createTimeLt != null) {
                orderListRequest.setCreateTimeLt(df.parse(createTimeLt).getTime() / 1000);
            }
            Map<String, String> parameters = new HashMap<>();
            parameters.put("page_size", pageSize);
            String nextPageToken = "";
            OrderListResponse response = Orders.getOrderList(info, parameters, orderListRequest);
            if (response.getCode() != 0) return Response.serverError().entity(response).build();
            nextPageToken = response.getData().getNextPageToken();
            while (!nextPageToken.isBlank()) {
                parameters.put("page_token", nextPageToken);
                OrderListResponse nextPageResponse = Orders.getOrderList(info, parameters, orderListRequest);
                int code = nextPageResponse.getCode();
                if (0 == code) {
                    response.combine(nextPageResponse);
                    nextPageToken = nextPageResponse.getData().getNextPageToken();
                } else {
                    System.err.println("Lỗi khi thực hiện call với next_page_token: " + nextPageToken);
                    try {
                        System.err.println(objectMapper.writeValueAsString(nextPageResponse));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    nextPageToken = "";
                }
            }

            Map<String, FormResult> donHangDoiSizeMau = new HashMap<>();
            ListFormResponsesResponse formResponses = GoogleDriveUtils.readFormResponses("12XXqkRR7j-Fl_EvkwzDdbgCI24ufohWFIKJWa2JWnQU");
            if (!formResponses.isEmpty()) {
                for (FormResponse formRes : formResponses.getResponses()) {
                    FormResult result = new FormResult(formRes);
                    if (StringUtils.isNotBlank(result.getOrderId())) {
                        donHangDoiSizeMau.put(result.getOrderId(), result);
                    } else System.err.println("Kết quả lấy từ form về bị lỗi: " + result);
                }
            }

            List<LineItem> lineItems = new ArrayList<>();
            Map<String, List<Order>> groupPackageByItem = new HashMap<>();
            Map<String, String> skuInfo = new HashMap<>(); // Map chứa thông tin về từng sản phẩm trong tất cả đơn hàng, key là product-skuId, value là tên, sku name
            List<String> ids = new ArrayList<>(); // List chứa danh sách product-skuId để đếm xem có bn sp, và mỗi sp xuất hiện bn lần
            int cancelOrder = 0;
            int cancelItem = 0;
            int changeInformation = 0;
            for (Order order : response.getData().getOrders()) {

                FormResult changeInfo = donHangDoiSizeMau.get(order.getId());
                int countChangeItemInOrder = 0;
                for (LineItem item : order.getLineItems()) {
                    // Check xem có phải đơn hàng bị thay đổi thông tin không thì override lại thông tin thành thông tin cần thay đổi
                    if (changeInfo != null) {
                        if (item.getSkuId().equals(changeInfo.getOriginalSkuId())) {
//                            skuInfo.putIfAbsent(item.getProductId() + "-" + changeInfo.getChangeSkuId(), item.getProductName() + ", " + changeInfo.getChaneSkuName());
//                            continue;
                            item.setSkuId(changeInfo.getChangeSkuId());
                            item.setSkuName(changeInfo.getChangeSkuName());
                            changeInformation++;
                            countChangeItemInOrder++;
                        }
                    }
                    skuInfo.putIfAbsent(item.getProductId() + "-" + item.getSkuId(), item.getProductName() + ", " + item.getSkuName());
                }

                // Warning khi thấy thông tin đơn hàng cần thay đổi nhưng không thấy sku cần thay đổi trong đơn do người nhập thông tin thay đổi không chính xác
                if (changeInfo != null && countChangeItemInOrder == 0)
                    System.err.println("Thông tin thay đổi của đơn hàng không chính xác, " + changeInfo + ". Vui lòng kiểm tra lại các sku trong đơn hàng!");

                if (order.getCancellationInitiator() == null && order.getCancelReason() == null) {
                    lineItems.addAll(order.getLineItems());
                    ids.addAll(order.getLineItems().stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList()));
                    String ident = null;
                    if (order.getLineItems().size() == 1) {
                        LineItem item = order.getLineItems().get(0);
                        ident = item.getProductId() + "-" + item.getSkuId();
                    } else {
                        ident = "donCoNhieuHon1SP";
                    }
                    if (groupPackageByItem.containsKey(ident)) {
                        List<Order> val = groupPackageByItem.get(ident);
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    } else {
                        List<Order> val = new ArrayList<>();
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    }
                } else {
                    cancelOrder += 1;
                    cancelItem += order.getLineItems().size();
                    System.err.println("Đơn hàng bị cancel: " + objectMapper.writeValueAsString(order));
                }
            }
            // tìm số lần xuất hiện của các phần tử
            Map<String, Integer> count = new TreeMap<String, Integer>();
            for (int j = 0; j < ids.size(); j++) {
                addElement(count, ids.get(j));
            }

            stringBuilder.append("Shop name: ").append(info.getName()).append("\n");
            stringBuilder.append("Thời gian tạo: ").append(startDate).append("\n");
            stringBuilder.append("Số đơn hàng thành công: ").append(response.getData().getOrders().size() - cancelOrder).append("(").append(lineItems.size()).append(" sản phẩm)").append("\n");
            stringBuilder.append("Số đơn hàng bị cancel: ").append(cancelItem).append("(").append(cancelItem).append(" sản phẩm)").append("\n");
            stringBuilder.append("Số đơn hàng bị thay đổi size, màu: ").append(changeInformation).append("\n");
            stringBuilder.append("====================================>>Số lượng đơn theo mỗi loại<<====================================").append("\n");
            for (String key : groupPackageByItem.keySet()) {
                String name = skuInfo.get(key) == null ? key : skuInfo.get(key);
                stringBuilder.append("donCoNhieuHon1SP".equals(key) ? "Đơn có nhiều sản phẩm" : name).append(": ").append(groupPackageByItem.get(key).size()).append("\n");
            }
            stringBuilder.append("====================================>>Số lượng cụ thể từng sku<<====================================").append("\n");
            for (String key : count.keySet()) {
                if (!skuInfo.containsKey(key)) {
                    stringBuilder.append("Không có key: ").append(key).append(" trong danh sách sku!\n");
                }
                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append("[").append(key.trim()).append("]: ").append(count.get(key)).append("\n");
//                List<Order> orderBySku = groupPackageByItem.get(key);
//                int orderBySkuSize = orderBySku == null ? count.get(key) : orderBySku.size();
//                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append(": ").append(count.get(key))
//                        .append("(huỷ: ").append(count.get(key) - orderBySkuSize).append(", còn lại: ").append(orderBySkuSize).append(")\n");
            }

            stringBuilder.append("====================================>>!<<====================================").append("\n");

        }

        return Response.ok(stringBuilder.toString()).build();
    }

    @GET
    @Path("/count1")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response count1(@QueryParam("code") List<String> shopCode,
                           @QueryParam("status") @DefaultValue("AWAITING_COLLECTION") String status,
                           @QueryParam("countLE") @DefaultValue("0") Integer countLE,
                           @QueryParam("pageSize") @DefaultValue("100") String pageSize,
                           @QueryParam("productSkuIds") List<String> productSkuIds) throws IOException {

        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
        //Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (list.isEmpty()) {
            return Response.serverError().build();
        }

        StringBuilder stringBuilder = new StringBuilder();
//        List<Sku> skus = skuRepository.listAll();

//        String dateFolder = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String startDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        for (ShopInfo info : list) {
            String resultFolderPath = "/Users/dle/IdeaProjects/tiktok/result/" + info.getName() + "/" + startDate + "/";
            OrderListRequest orderListRequest = new OrderListRequest();
            orderListRequest.setOrderStatus(status);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("page_size", pageSize);
            String nextPageToken = "";
            OrderListResponse response = Orders.getOrderList(info, parameters, orderListRequest);
            if (response.getCode() != 0) return Response.serverError().entity(response).build();
            nextPageToken = response.getData().getNextPageToken();
            while (!nextPageToken.isBlank()) {
                parameters.put("page_token", nextPageToken);
                OrderListResponse nextPageResponse = Orders.getOrderList(info, parameters, orderListRequest);
                int code = nextPageResponse.getCode();
                if (0 == code) {
                    response.combine(nextPageResponse);
                    nextPageToken = nextPageResponse.getData().getNextPageToken();
                } else {
                    System.err.println("Lỗi khi thực hiện call với next_page_token: " + nextPageToken);
                    try {
                        System.err.println(objectMapper.writeValueAsString(nextPageResponse));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    nextPageToken = "";
                }
            }

            // Lấy thông tin đổi size, màu trên form
            Map<String, FormResult> donHangDoiSizeMau = new HashMap<>(); //Map chứa thông tin đơn hàng cần thay đổi thông tin, chưa rõ đúng sai!
            ListFormResponsesResponse formResponses = GoogleDriveUtils.readFormResponses("12XXqkRR7j-Fl_EvkwzDdbgCI24ufohWFIKJWa2JWnQU");
            if (!formResponses.isEmpty()) {
                for (FormResponse formRes : formResponses.getResponses()) {
                    FormResult result = new FormResult(formRes);
                    if (StringUtils.isNotBlank(result.getOrderId())) {
                        donHangDoiSizeMau.put(result.getOrderId(), result);
                    } else System.err.println("Kết quả lấy từ form về bị lỗi: " + result);
                }
            }

            List<LineItem> lineItems = new ArrayList<>();
            Map<String, List<Order>> groupPackageByItem = new HashMap<>();
            Map<String, String> skuInfo = new HashMap<>(); // Map chứa thông tin về từng sản phẩm trong tất cả đơn hàng, key là product-skuId, value là tên, sku name
            List<String> ids = new ArrayList<>(); // List chứa danh sách product-skuId để đếm xem có bn sp, và mỗi sp xuất hiện bn lần
            int cancelOrder = 0;
            int cancelItem = 0;
            int changeInformation = 0;
            Map<String, FormResult> donHangDoiSizeMauCorrect = new HashMap<>(); //Map chứa danh sách đơn hàng cần thay đổi thông tin chính xác, khớp với dữ liệu đơn hàng lấy dc

            forAllOrder:
            for (Order order : response.getData().getOrders()) {

                FormResult changeInfo = donHangDoiSizeMau.get(order.getId());
                int countChangeItemInOrder = 0;
                for (LineItem item : order.getLineItems()) {
                    // Check xem có phải đơn hàng bị thay đổi thông tin không thì override lại thông tin thành thông tin cần thay đổi
                    if (changeInfo != null) {
                        if (item.getSkuId().equals(changeInfo.getOriginalSkuId())) {
                            item.setSkuId(changeInfo.getChangeSkuId());
                            item.setSkuName(changeInfo.getChangeSkuName());
                            changeInformation++;
                            countChangeItemInOrder++;
                            donHangDoiSizeMauCorrect.put(order.getId(), changeInfo);
                        }
                    }
                    skuInfo.putIfAbsent(item.getProductId() + "-" + item.getSkuId(), item.getProductName() + ", " + item.getSkuName());
                }

                // Warning khi thấy thông tin đơn hàng cần thay đổi nhưng không thấy sku cần thay đổi trong đơn do người nhập thông tin thay đổi không chính xác
                if (changeInfo != null && countChangeItemInOrder == 0)
                    System.err.println("Thông tin thay đổi của đơn hàng không chính xác, " + changeInfo + ". Vui lòng kiểm tra lại các sku trong đơn hàng!");

                if (order.getCancellationInitiator() == null && order.getCancelReason() == null) {
                    lineItems.addAll(order.getLineItems());
                    ids.addAll(order.getLineItems().stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList()));
                    String ident = null;
                    if (order.getLineItems().size() == 1) {
                        LineItem item = order.getLineItems().get(0);
                        ident = item.getProductId() + "-" + item.getSkuId();
                    } else {
                        ident = "donCoNhieuHon1SP";
                    }
                    if (groupPackageByItem.containsKey(ident)) {
                        List<Order> val = groupPackageByItem.get(ident);
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    } else {
                        List<Order> val = new ArrayList<>();
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    }
                } else {
                    cancelOrder += 1;
                    cancelItem += order.getLineItems().size();
//                    System.err.println("Đơn hàng bị cancel: " + objectMapper.writeValueAsString(order));
                    String lineItem = order.getLineItems().stream().map(e -> e.getProductName() + "(" + e.getSkuName() + ")").collect(Collectors.joining(","));
                    System.err.println(String.format("Đơn hàng %s; gồm các sp: %s; tạo ngày '%s'; bị huỷ với lí do '%s'; bởi '%s'; cập nhật lúc: %s",
                            order.getId(), lineItem, new Date(order.getCreateTime() * 1000), order.getCancelReason(), order.getCancellationInitiator(), new Date(order.getUpdateTime() * 1000)));
                }
            }

            Map<String, String> documentParams = new HashMap<>();
            documentParams.put("document_type", "SHIPPING_LABEL_AND_PACKING_SLIP");
            documentParams.put("document_size", "A6");
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Di chuyển các folder của các ngày trước đó (giữ 3 ngày gần nhất) vào thư mục Archived
            LocalDateTime now = LocalDateTime.now();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String nowDateFolder = dateFormat.format(java.sql.Timestamp.valueOf(now));
            String nowDateFolder_1 = dateFormat.format(java.sql.Timestamp.valueOf(now.minusDays(1)));
            String nowDateFolder_2 = dateFormat.format(java.sql.Timestamp.valueOf(now.minusDays(2)));
            List<com.google.api.services.drive.model.File> googleShopFolders = GoogleDriveUtils.getGoogleSubFolders(getFolderIdByShop(info.getCode()));
            String archivedFolderId = getArchiveFolderIdByShop(info.getCode());
            for (com.google.api.services.drive.model.File folder : googleShopFolders) {
                if (archivedFolderId.equals(folder.getId())) continue;
                if (!folder.getName().startsWith(nowDateFolder) && !folder.getName().startsWith(nowDateFolder_1) && !folder.getName().startsWith(nowDateFolder_2)) {
                    List<String> moveToParentId = GoogleDriveUtils.moveFileToFolder(folder.getId(), archivedFolderId);
                    System.out.println("Move Folder ID: " + folder.getId() + " --- Name: " + folder.getName() + " To Folder: Archived" + moveToParentId);
                }
            }

            com.google.api.services.drive.model.File shopFolder = GoogleDriveUtils.createGoogleFolder(getFolderIdByShop(info.getCode()), startDate);

            // Create Folder chứa kết quả của request, bao gồm pdf, response, order success list
            Files.createDirectories(Paths.get(resultFolderPath));

            List<String> orderSuccess = new ArrayList<>();

            for (String keyProductId_SkuId : groupPackageByItem.keySet()) {
                if (!productSkuIds.isEmpty() && !productSkuIds.contains(keyProductId_SkuId)) {
                    System.out.printf("Đơn hàng chứa sp: %s[%s] sẽ không được in do sp cần in là: %s \n", skuInfo.get(keyProductId_SkuId), keyProductId_SkuId, productSkuIds);
                    continue;
                }
                List<Order> orders = groupPackageByItem.get(keyProductId_SkuId);
                PDFMergerUtility ut = new PDFMergerUtility();
                int countPdfPage = 0;
                int printedCount = 0;
                List<Document> documentList = new ArrayList<>();
                for (Order order : orders) {
                    Set<String> packageIds = order.getPackages().stream().map(Package::getId).collect(Collectors.toSet());
                    for (String packageId : packageIds) {
                        String combineOrderPackageId = order.getId() + "-" + packageId;
                        Optional<Document> documentOptional = documentRepository.find("shopCode = ?1 and packageId = ?2", info.getCode(), combineOrderPackageId).singleResultOptional();
                        if (documentOptional.isEmpty() || documentOptional.get().getCount() <= countLE) {
                            Document document = documentOptional.isEmpty() ? new Document(combineOrderPackageId, info.getCode(), new Date()) : documentOptional.get();
                            ShippingDocumentResponse documentResponse = Orders.getShippingDocument(info, documentParams, packageId);
                            int tryCount = 3;
                            while (tryCount != 0 && documentResponse.getCode() != 0) {
                                System.err.println(String.format("Tạo document lần %d có packageId là %s thất bại với lí do: %s, thử lại sau 1s", (3 - tryCount) + 1, combineOrderPackageId, objectMapper.writeValueAsString(documentResponse)));
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    System.err.println("Lỗi thread sleep 1s: " + e.getMessage());
                                }
                                documentResponse = Orders.getShippingDocument(info, documentParams, packageId);
//                            documentResponse = Orders.getShippingDocument1(httpClient, info, documentParams, packageId);
                                tryCount--;
                            }
                            if (documentResponse.getCode() == 0) {
                                if (tryCount < 3)
                                    System.err.println("Thành công sau " + (3 - tryCount) + " lần thử lại!");
                                document.setDocUrl(documentResponse.getData().getDocUrl());
                                HttpGet httpget = new HttpGet(documentResponse.getData().getDocUrl());
                                try {
                                    HttpResponse res = null;
                                    try {
                                        res = httpClient.execute(httpget);
                                    } catch (IOException e) {
                                        System.err.print("Tải PDF thất bại cho order: " + combineOrderPackageId + ", sẽ thử lại lần nữa sau 1s! ");
                                        System.err.println("Chi tiết lỗi: " + e.getMessage());
                                        Thread.sleep(1000);
                                        res = httpClient.execute(httpget);
                                    }
                                    HttpEntity entity = res.getEntity();
                                    if (entity != null) {
//                                InputStream inputStream = entity.getContent();
                                        File file = new File("/Users/dle/IdeaProjects/tiktok/temp/" + combineOrderPackageId + ".pdf");
//                                inputStream.transferTo(new FileOutputStream(file, false));
//                                Files.copy(inputStream, Paths.get(filePathString), StandardCopyOption.REPLACE_EXISTING);

                                        byte[] inputPdf = entity.getContent().readAllBytes();
                                        //byte[] inputPdfToTry = inputPdf.clone();
                                        try {
//                                            writeMarkup2(inputPdf, file); //Thêm dòng: quay video khi bóc hàng vào cuối file in
                                            writeMarkup3(inputPdf, file, donHangDoiSizeMauCorrect.get(order.getId())); //Thêm dòng: quay video khi bóc hàng vào cuối file in
                                            ut.addSource(file);
                                            orderSuccess.add(order.getId());
                                        } catch (IOException e) {
                                            System.out.printf("Thêm text vào file pdf thất bại(lí do: %s), order: %s, sẽ tải lại file và thử lại lần nữa sau 1s!! ", e.getMessage(), combineOrderPackageId);
                                            try {
                                                Thread.sleep(1000);
                                                res = httpClient.execute(httpget);
                                                HttpEntity entity1 = res.getEntity();
                                                if (entity1 != null) {
                                                    File tryFile = new File("/Users/dle/IdeaProjects/tiktok/temp/" + combineOrderPackageId + "_try.pdf");
                                                    byte[] inputPdfToTry = entity1.getContent().readAllBytes();
//                                                    writeMarkup2(inputPdfToTry, tryFile);
                                                    writeMarkup3(inputPdfToTry, tryFile, donHangDoiSizeMauCorrect.get(order.getId()));
                                                    ut.addSource(tryFile);
                                                    orderSuccess.add(order.getId());
                                                    System.out.println("Thêm text vào file pdf thành công sau 1 lần thử lại!");
                                                } else {
                                                    System.out.printf("Quá trình tải lại pdf thất bại vì entity null: %s \n", objectMapper.writeValueAsString(res.getEntity()));
                                                }
                                            } catch (IOException ioException) {
                                                System.out.printf("Quá trình tải lại pdf và thêm text thất bại với lí do: %s \n", ioException.getMessage());
                                            }

                                        }

                                        countPdfPage++;
                                        document.setCount(document.getCount() + 1);
                                        document.setDownloadDate(new Date());
                                        document.setErrorMessage(null);
                                        System.out.print("\t.");
                                    }
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                    document.setErrorMessage(e.getMessage());
                                    System.err.println("Đã tạo được document nhưng không thể tải về với lí do: " + e.getMessage());
                                }
                            } else {
//                            String errorMessage = objectMapper.writeValueAsString(documentResponse);
                                String errorMessage = String.format("Không thể tạo document có packageId là %s với lí do: %s", combineOrderPackageId, objectMapper.writeValueAsString(documentResponse));
                                document.setErrorMessage(errorMessage);
                                System.err.println(errorMessage);
                            }
                            documentList.add(document);
//                            documentRepository.persistAndFlush(document);
                        } else {
                            printedCount++;
                            System.err.println(String.format("Đơn hàng: %s, packageId: %s đã được in %d lần, sẽ bị bỏ qua !!!", order.getId(), packageId, documentOptional.get().getCount()));
                        }
                    }
                }

                documentRepository.persist(documentList);

                if (countPdfPage > 0) {
                    String name = null;
                    if (printedCount != 0) {
                        name = String.format("%s(%d đơn, đã in trước đó: %d, còn lại: %d).pdf", skuInfo.get(keyProductId_SkuId) != null ? skuInfo.get(keyProductId_SkuId) : keyProductId_SkuId, orders.size(), printedCount, orders.size() - printedCount);
                    } else {
                        name = String.format("%s(%d đơn).pdf", skuInfo.get(keyProductId_SkuId) != null ? skuInfo.get(keyProductId_SkuId) : keyProductId_SkuId, orders.size());
                    }
                    ut.setDestinationFileName(resultFolderPath + name);
                    ut.mergeDocuments(null);
                    System.out.println("\n" + ut.getDestinationFileName());
                    GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "application/pdf", name, new File(ut.getDestinationFileName()));
                    System.out.println("====================================>>!<<====================================");
                }
            }

            //documentRepository.persist(documentList);
            String successOrderIdListName = "successPrintedOrderIdList(" + orderSuccess.size() + ").txt";
            java.nio.file.Path filePath = Paths.get(resultFolderPath + successOrderIdListName);
//            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            for (String str : orderSuccess) {
                Files.writeString(filePath, str + System.lineSeparator(),
                        StandardOpenOption.APPEND);
            }
            GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "text/plain", successOrderIdListName, filePath.toFile());
            System.out.println("Doneeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee!\t" + filePath.toString());
//            httpClient.close();

//            List<String> ids = lineItems.stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList());

            // tìm số lần xuất hiện của các phần tử
            Map<String, Integer> count = new TreeMap<String, Integer>();
            for (int j = 0; j < ids.size(); j++) {
                addElement(count, ids.get(j));
            }

            stringBuilder.append("Shop name: ").append(info.getName()).append("\n");
            stringBuilder.append("Thời gian tạo: ").append(startDate).append("\n");
            stringBuilder.append("Số đơn hàng thành công: ").append(response.getData().getOrders().size() - cancelOrder).append("(").append(lineItems.size()).append(" sản phẩm)").append("\n");
            stringBuilder.append("Số đơn hàng bị cancel: ").append(cancelItem).append("(").append(cancelItem).append(" sản phẩm)").append("\n");
            stringBuilder.append("====================================>>Số lượng đơn theo mỗi loại<<====================================").append("\n");
            for (String key : groupPackageByItem.keySet()) {
                String name = skuInfo.get(key) == null ? key : skuInfo.get(key);
                stringBuilder.append("donCoNhieuHon1SP".equals(key) ? "Đơn có nhiều sản phẩm" : name).append(": ").append(groupPackageByItem.get(key).size()).append("\n");
            }
            stringBuilder.append("====================================>>Số lượng cụ thể từng sku<<====================================").append("\n");
            for (String key : count.keySet()) {
                if (!skuInfo.containsKey(key)) {
                    stringBuilder.append("Không có key: ").append(key).append(" trong danh sách sku!\n");
                }
                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append(": ").append(count.get(key)).append("\n");
            }

            if (!productSkuIds.isEmpty()) {
                stringBuilder.append("====================================>>!<<====================================").append("\n");
                stringBuilder.append("Chỉ in các đơn hàng chứa các sản phẩm sau: \n");
                productSkuIds.forEach(e -> {
                    stringBuilder.append("\t- ").append("donCoNhieuHon1SP".equals(e) ? "Đơn có nhiều sản phẩm" : (skuInfo.get(e) == null ? e : skuInfo.get(e))).append(": ").append(groupPackageByItem.get(e).size()).append("\n");
                });
            }

            if (!donHangDoiSizeMauCorrect.isEmpty()) {
                stringBuilder.append("====================================>>!<<====================================").append("\n");
                stringBuilder.append("Các đơn hàng đã thay đổi thông tin: \n");
                for (FormResult formResult : donHangDoiSizeMauCorrect.values()) {
                    stringBuilder.append(formResult.getOrderId()).append(": ").append(formResult.getProductName()).append(", ").append(formResult.getOriginalSkuName()).append(" -> ").append(formResult.getChangeSkuName()).append("\n");
                }
            }

            Arrays.stream(new File("/Users/dle/IdeaProjects/tiktok/temp/").listFiles()).forEach(File::delete);
//            Arrays.stream(new File("/Users/dle/IdeaProjects/tiktok/result/").listFiles()).forEach(File::delete);
            GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "text/plain", "Info.txt", stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            FileUtils.writeStringToFile(new File(resultFolderPath + "Info.txt"), stringBuilder.toString(), "UTF-8");

        }

        return Response.ok(stringBuilder.toString()).build();
    }


    @GET
    @Path("/count2")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response count2(@QueryParam("code") List<String> shopCode,
                           @QueryParam("status") @DefaultValue("AWAITING_COLLECTION") String status,
                           @QueryParam("countLE") @DefaultValue("0") Integer countLE,
                           @QueryParam("pageSize") @DefaultValue("100") String pageSize,
                           @QueryParam("productSkuIds") List<String> productSkuIds,
                           @QueryParam("fileComparePath") String fileComparePath) throws IOException {

        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
        //Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (list.isEmpty()) {
            return Response.serverError().build();
        }

        StringBuilder stringBuilder = new StringBuilder();
//        List<Sku> skus = skuRepository.listAll();

//        String dateFolder = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String startDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        for (ShopInfo info : list) {
            String resultFolderPath = "/Users/dle/IdeaProjects/tiktok/result/" + info.getName() + "/" + startDate + "/";
            OrderListRequest orderListRequest = new OrderListRequest();
            orderListRequest.setOrderStatus(status);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("page_size", pageSize);
            String nextPageToken = "";
            OrderListResponse response = Orders.getOrderList(info, parameters, orderListRequest);
            if (response.getCode() != 0) return Response.serverError().entity(response).build();
            nextPageToken = response.getData().getNextPageToken();
            while (!nextPageToken.isBlank()) {
                parameters.put("page_token", nextPageToken);
                OrderListResponse nextPageResponse = Orders.getOrderList(info, parameters, orderListRequest);
                int code = nextPageResponse.getCode();
                if (0 == code) {
                    response.combine(nextPageResponse);
                    nextPageToken = nextPageResponse.getData().getNextPageToken();
                } else {
                    System.err.println("Lỗi khi thực hiện call với next_page_token: " + nextPageToken);
                    try {
                        System.err.println(objectMapper.writeValueAsString(nextPageResponse));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    nextPageToken = "";
                }
            }

            // Lấy thông tin đổi size, màu trên form
            Map<String, FormResult> donHangDoiSizeMau = new HashMap<>(); //Map chứa thông tin đơn hàng cần thay đổi thông tin, chưa rõ đúng sai!
            ListFormResponsesResponse formResponses = GoogleDriveUtils.readFormResponses("12XXqkRR7j-Fl_EvkwzDdbgCI24ufohWFIKJWa2JWnQU");
            if (!formResponses.isEmpty()) {
                for (FormResponse formRes : formResponses.getResponses()) {
                    FormResult result = new FormResult(formRes);
                    if (StringUtils.isNotBlank(result.getOrderId())) {
                        donHangDoiSizeMau.put(result.getOrderId(), result);
                    } else System.err.println("Kết quả lấy từ form về bị lỗi: " + result);
                }
            }

            List<LineItem> lineItems = new ArrayList<>();
            Map<String, List<Order>> groupPackageByItem = new HashMap<>();
            Map<String, String> skuInfo = new HashMap<>(); // Map chứa thông tin về từng sản phẩm trong tất cả đơn hàng, key là product-skuId, value là tên, sku name
            List<String> ids = new ArrayList<>(); // List chứa danh sách product-skuId để đếm xem có bn sp, và mỗi sp xuất hiện bn lần
            int cancelOrder = 0;
            int cancelItem = 0;
            int changeInformation = 0;
            Map<String, FormResult> donHangDoiSizeMauCorrect = new HashMap<>(); //Map chứa danh sách đơn hàng cần thay đổi thông tin chính xác, khớp với dữ liệu đơn hàng lấy dc

            forAllOrder:
            for (Order order : response.getData().getOrders()) {

                FormResult changeInfo = donHangDoiSizeMau.get(order.getId());
                int countChangeItemInOrder = 0;
                for (LineItem item : order.getLineItems()) {
                    // Check xem có phải đơn hàng bị thay đổi thông tin không thì override lại thông tin thành thông tin cần thay đổi
                    if (changeInfo != null) {
                        if (item.getSkuId().equals(changeInfo.getOriginalSkuId())) {
                            item.setSkuId(changeInfo.getChangeSkuId());
                            item.setSkuName(changeInfo.getChangeSkuName());
                            changeInformation++;
                            countChangeItemInOrder++;
                            donHangDoiSizeMauCorrect.put(order.getId(), changeInfo);
                        }
                    }
                    skuInfo.putIfAbsent(item.getProductId() + "-" + item.getSkuId(), item.getProductName() + ", " + item.getSkuName());
                }

                // Warning khi thấy thông tin đơn hàng cần thay đổi nhưng không thấy sku cần thay đổi trong đơn do người nhập thông tin thay đổi không chính xác
                if (changeInfo != null && countChangeItemInOrder == 0)
                    System.err.println("Thông tin thay đổi của đơn hàng không chính xác, " + changeInfo + ". Vui lòng kiểm tra lại các sku trong đơn hàng!");

                if (order.getCancellationInitiator() == null && order.getCancelReason() == null) {
                    lineItems.addAll(order.getLineItems());
                    ids.addAll(order.getLineItems().stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList()));
                    String ident = null;
                    if (order.getLineItems().size() == 1) {
                        LineItem item = order.getLineItems().get(0);
                        ident = item.getProductId() + "-" + item.getSkuId();
                    } else {
                        ident = "donCoNhieuHon1SP";
                    }
                    if (groupPackageByItem.containsKey(ident)) {
                        List<Order> val = groupPackageByItem.get(ident);
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    } else {
                        List<Order> val = new ArrayList<>();
                        val.add(order);
                        groupPackageByItem.put(ident, val);
                    }
                } else {
                    cancelOrder += 1;
                    cancelItem += order.getLineItems().size();
//                    System.err.println("Đơn hàng bị cancel: " + objectMapper.writeValueAsString(order));
                    String lineItem = order.getLineItems().stream().map(e -> e.getProductName() + "(" + e.getSkuName() + ")").collect(Collectors.joining(","));
                    System.err.println(String.format("Đơn hàng %s; gồm các sp: %s; tạo ngày '%s'; bị huỷ với lí do '%s'; bởi '%s'; cập nhật lúc: %s",
                            order.getId(), lineItem, new Date(order.getCreateTime() * 1000), order.getCancelReason(), order.getCancellationInitiator(), new Date(order.getUpdateTime() * 1000)));
                }
            }

//            Map<String, String> documentParams = Collections.synchronizedMap(new HashMap<>());
//            documentParams.put("document_type", "SHIPPING_LABEL_AND_PACKING_SLIP");
//            documentParams.put("document_size", "A6");

            // Di chuyển các folder của các ngày trước đó (giữ 3 ngày gần nhất) vào thư mục Archived
            LocalDateTime now = LocalDateTime.now();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String nowDateFolder = dateFormat.format(java.sql.Timestamp.valueOf(now));
            String nowDateFolder_1 = dateFormat.format(java.sql.Timestamp.valueOf(now.minusDays(1)));
            String nowDateFolder_2 = dateFormat.format(java.sql.Timestamp.valueOf(now.minusDays(2)));
            List<com.google.api.services.drive.model.File> googleShopFolders = GoogleDriveUtils.getGoogleSubFolders(getFolderIdByShop(info.getCode()));
            String archivedFolderId = getArchiveFolderIdByShop(info.getCode());
            for (com.google.api.services.drive.model.File folder : googleShopFolders) {
                if (archivedFolderId.equals(folder.getId())) continue;
                if (!folder.getName().startsWith(nowDateFolder) && !folder.getName().startsWith(nowDateFolder_1) && !folder.getName().startsWith(nowDateFolder_2)) {
                    List<String> moveToParentId = GoogleDriveUtils.moveFileToFolder(folder.getId(), archivedFolderId);
                    System.out.println("Move Folder ID: " + folder.getId() + " --- Name: " + folder.getName() + " To Folder: Archived" + moveToParentId);
                }
            }

            com.google.api.services.drive.model.File shopFolder = GoogleDriveUtils.createGoogleFolder(getFolderIdByShop(info.getCode()), startDate);

            // Create Folder chứa kết quả của request, bao gồm pdf, response, order success list
            Files.createDirectories(Paths.get(resultFolderPath));

            List<String> orderSuccess = Collections.synchronizedList(new ArrayList<>());

            // task executor cho việc lặp qua danh sách sản phẩm và tạo file in
//            ExecutorService forAllProductTaskExecutor = Executors.newFixedThreadPool(5);
//            CountDownLatch allProductLatch = new CountDownLatch(groupPackageByItem.size());

            // task executor cho việc download và thêm text vào đơn
            ExecutorService downloadAndEditTaskExecutor = Executors.newFixedThreadPool(15);

            List<Document> documents = documentRepository.list("shopCode = ?1 and downloadDate > ?2", info.getCode(), java.sql.Timestamp.valueOf(now.minusDays(5)));
            Map<String, Document> documentMap = documents.stream().collect(Collectors.toUnmodifiableMap(Document::getPackageId, Function.identity(), (first, second) -> first));

            // Lấy d/s đơn hàng đã in dạng file để so sánh, nếu đã in r thì thôi, nếu k muốn check theo file thì đề fileComparePath là null
            Map<String, String> orderIdDaIn = new HashMap<>();
            if (StringUtils.isNotBlank(fileComparePath)) {
                try (Stream<String> stream = Files.lines(Paths.get(fileComparePath), Charset.defaultCharset())) {
                    stream.forEach(e -> orderIdDaIn.put(e, e));
                } catch (IOException e) {
                    System.err.println("Lỗi khi đọc file order id để sánh: " + e.getMessage());
                    throw e;
                }
            }

            for (String keyProductId_SkuId : groupPackageByItem.keySet()) {
                if (!productSkuIds.isEmpty() && !productSkuIds.contains(keyProductId_SkuId)) {
                    System.out.printf("Đơn hàng chứa sp: %s[%s] sẽ không được in do sp cần in là: %s \n", skuInfo.get(keyProductId_SkuId), keyProductId_SkuId, productSkuIds);
                    continue;
                }
//                forAllProductTaskExecutor.execute(() -> {
                List<Order> orders = groupPackageByItem.get(keyProductId_SkuId);
                PDFMergerUtility ut = new PDFMergerUtility();
                AtomicInteger countPdfPage = new AtomicInteger(0);
                AtomicInteger printedCount = new AtomicInteger(0);
                CountDownLatch allOrderLatch = new CountDownLatch(orders.size());
                List<Document> documentList = Collections.synchronizedList(new ArrayList<>());
                for (Order order : orders) {
                    Set<String> packageIds = order.getPackages().stream().map(Package::getId).collect(Collectors.toSet());
                    for (String packageId : packageIds) {
                        downloadAndEditTaskExecutor.execute(() -> {
                            String combineOrderPackageId = order.getId() + "-" + packageId;
                            Document documentInDb = documentMap.get(combineOrderPackageId);
                            if ((documentInDb == null || documentInDb.getCount() <= countLE) && !orderIdDaIn.containsKey(order.getId())) {
                                Document document = documentInDb == null ? new Document(combineOrderPackageId, info.getCode(), new Date()) : documentInDb;

                                Map<String, String> documentParams = new HashMap<>();
                                documentParams.put("document_type", "SHIPPING_LABEL_AND_PACKING_SLIP");
                                documentParams.put("document_size", "A6");

                                ShippingDocumentResponse documentResponse = Orders.getShippingDocument1(info, documentParams, packageId, httpclient);

                                int tryCount = 3;
                                while (tryCount != 0 && documentResponse.getCode() != 0) {
                                    try {
                                        System.err.println(String.format("Tạo document lần %d có packageId là %s thất bại với lí do: %s, thử lại sau 1s", (3 - tryCount) + 1, combineOrderPackageId, objectMapper.writeValueAsString(documentResponse)));
                                    } catch (JsonProcessingException e) {
//                                        throw new RuntimeException(e);
                                        e.printStackTrace();
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        System.err.println("Lỗi thread sleep 1s: " + e.getMessage());
                                    }
                                    documentResponse = Orders.getShippingDocument1(info, documentParams, packageId, httpclient);
//                            documentResponse = Orders.getShippingDocument1(httpClient, info, documentParams, packageId);
                                    tryCount--;
                                }
                                if (documentResponse.getCode() == 0) {
                                    if (tryCount < 3)
                                        System.err.println("Thành công sau " + (3 - tryCount) + " lần thử lại!");
                                    document.setDocUrl(documentResponse.getData().getDocUrl());
                                    HttpGet httpget = new HttpGet(documentResponse.getData().getDocUrl());
                                    try {
                                        HttpResponse res = null;
                                        try {
                                            res = httpclient.execute(httpget);
                                        } catch (IOException e) {
                                            System.err.print("Tải PDF thất bại cho order: " + combineOrderPackageId + ", sẽ thử lại lần nữa sau 1s! ");
                                            System.err.println("Chi tiết lỗi: " + e.getMessage());
                                            Thread.sleep(1000);
                                            res = httpclient.execute(httpget);
                                        }
                                        HttpEntity entity = res.getEntity();
                                        if (entity != null) {
//                                InputStream inputStream = entity.getContent();
                                            File file = new File("/Users/dle/IdeaProjects/tiktok/temp/" + combineOrderPackageId + ".pdf");
//                                inputStream.transferTo(new FileOutputStream(file, false));
//                                Files.copy(inputStream, Paths.get(filePathString), StandardCopyOption.REPLACE_EXISTING);

                                            byte[] inputPdf = entity.getContent().readAllBytes();
                                            //byte[] inputPdfToTry = inputPdf.clone();
                                            try {
//                                            writeMarkup2(inputPdf, file); //Thêm dòng: quay video khi bóc hàng vào cuối file in
                                                writeMarkup3(inputPdf, file, donHangDoiSizeMauCorrect.get(order.getId())); //Thêm dòng: quay video khi bóc hàng vào cuối file in
                                                ut.addSource(file);
                                                orderSuccess.add(order.getId());
                                            } catch (IOException e) {
                                                System.out.printf("Thêm text vào file pdf thất bại(lí do: %s), order: %s, sẽ tải lại file và thử lại lần nữa sau 1s!! ", e.getMessage(), combineOrderPackageId);
                                                try {
                                                    Thread.sleep(1000);
                                                    res = httpclient.execute(httpget);
                                                    HttpEntity entity1 = res.getEntity();
                                                    if (entity1 != null) {
                                                        File tryFile = new File("/Users/dle/IdeaProjects/tiktok/temp/" + combineOrderPackageId + "_try.pdf");
                                                        byte[] inputPdfToTry = entity1.getContent().readAllBytes();
//                                                    writeMarkup2(inputPdfToTry, tryFile);
                                                        writeMarkup3(inputPdfToTry, tryFile, donHangDoiSizeMauCorrect.get(order.getId()));
                                                        ut.addSource(tryFile);
                                                        orderSuccess.add(order.getId());
                                                        System.out.println("Thêm text vào file pdf thành công sau 1 lần thử lại!");
                                                    } else {
                                                        System.out.printf("Quá trình tải lại pdf thất bại vì entity null: %s \n", objectMapper.writeValueAsString(res.getEntity()));
                                                    }
                                                } catch (IOException ioException) {
                                                    System.out.printf("Quá trình tải lại pdf và thêm text thất bại với lí do: %s \n", ioException.getMessage());
                                                }

                                            }

                                            countPdfPage.incrementAndGet();
                                            document.setCount(document.getCount() + 1);
                                            document.setDownloadDate(new Date());
                                            document.setErrorMessage(null);
                                            System.out.print("\t.");
                                        }
                                    } catch (Exception e) {
                                        //e.printStackTrace();
                                        document.setErrorMessage(e.getMessage());
                                        System.err.println("Đã tạo được document nhưng không thể tải về với lí do: " + e.getMessage());
                                    }
                                } else {
//                            String errorMessage = objectMapper.writeValueAsString(documentResponse);
                                    String errorMessage = null;
                                    try {
                                        errorMessage = String.format("Không thể tạo document có packageId là %s với lí do: %s", combineOrderPackageId, objectMapper.writeValueAsString(documentResponse));
                                    } catch (JsonProcessingException e) {
//                                        throw new RuntimeException(e);
                                        e.printStackTrace();
                                    }
                                    document.setErrorMessage(errorMessage);
                                    System.err.println(errorMessage);
                                }
                                documentList.add(document);
//                                documentRepository.persistAndFlush(document);
                            } else {
                                printedCount.incrementAndGet();
                                System.err.println(String.format("Đơn hàng: %s, packageId: %s đã được in %d lần, sẽ bị bỏ qua !!!", order.getId(), packageId, documentInDb == null ? 100 : documentInDb.getCount()));
                                if (documentInDb == null) {
                                    Document document = new Document();
                                    document.setPackageId(combineOrderPackageId);
                                    document.setCount(100);
                                    document.setShopCode(info.getCode());
                                    document.setDownloadDate(new Date());
                                    documentList.add(document);
                                }
                            }
                            allOrderLatch.countDown();
                        });
                    }
                }

                // Đợi việc tải về xong xuôi r mới tạo file pdf
                try {
                    allOrderLatch.await();
                } catch (InterruptedException E) {
                    // handle
                    E.printStackTrace();
                }

                if (countPdfPage.get() > 0) {
                    String name = null;
                    if (printedCount.get() != 0) {
                        name = String.format("%s(%d đơn, đã in trước đó: %d, còn lại: %d).pdf", skuInfo.get(keyProductId_SkuId) != null ? skuInfo.get(keyProductId_SkuId) : keyProductId_SkuId, orders.size(), printedCount.get(), orders.size() - printedCount.get());
                    } else {
                        name = String.format("%s(%d đơn).pdf", skuInfo.get(keyProductId_SkuId) != null ? skuInfo.get(keyProductId_SkuId) : keyProductId_SkuId, orders.size());
                    }
                    ut.setDestinationFileName(resultFolderPath + name);
                    try {
                        ut.mergeDocuments(null);
                        System.out.println("\n" + ut.getDestinationFileName());
                        GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "application/pdf", name, new File(ut.getDestinationFileName()));
                        System.out.println("====================================>>!<<====================================");
                    } catch (IOException e) {
//                            throw new RuntimeException(e);
                        System.err.println("Quá trình merge file pdf: " + name + " thất bại, " + e.getMessage());
                    }

                }

                // Lưu log document vào db
                documentRepository.persist(documentList);
//                    allProductLatch.countDown();
//                });
            }

//            try {
//                allProductLatch.await();
//            } catch (InterruptedException e) {
////                throw new RuntimeException(e);
//                e.printStackTrace();
//            }

            //documentRepository.persist(documentList);
            String successOrderIdListName = "successPrintedOrderIdList(" + orderSuccess.size() + ").txt";
            java.nio.file.Path filePath = Paths.get(resultFolderPath + successOrderIdListName);
//            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            for (String str : orderSuccess) {
                Files.writeString(filePath, str + System.lineSeparator(),
                        StandardOpenOption.APPEND);
            }
            GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "text/plain", successOrderIdListName, filePath.toFile());

            PDFMergerUtility mergeAllPdf = new PDFMergerUtility();
            try {
                Arrays.stream(new File(resultFolderPath).listFiles()).forEach(file -> {
                    if (file.getName().endsWith(".pdf")) {
                        try {
                            mergeAllPdf.addSource(file);
                        } catch (FileNotFoundException e) {
                            System.err.println("Lỗi khi merge all file pdf, " + e.getMessage());
                        }
                    }
                });
                mergeAllPdf.setDestinationFileName(resultFolderPath + "Tất cả đơn.pdf");
                mergeAllPdf.mergeDocuments(null);
                GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "application/pdf", "Tất cả đơn.pdf", new File(mergeAllPdf.getDestinationFileName()));
            } catch (Exception ex) {
                System.err.println("Lỗi khi merge all file pdf, " + ex.getMessage());
            }
            System.out.println("Doneeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee!\t\t\t" + filePath.toString());
//            httpClient.close();

            // Tat cac executor!
//            forAllProductTaskExecutor.shutdown();
            downloadAndEditTaskExecutor.shutdown();

//            List<String> ids = lineItems.stream().map(e -> e.getProductId() + "-" + e.getSkuId()).collect(Collectors.toList());

            // tìm số lần xuất hiện của các phần tử
            Map<String, Integer> count = new TreeMap<String, Integer>();
            for (int j = 0; j < ids.size(); j++) {
                addElement(count, ids.get(j));
            }

            stringBuilder.append("Shop name: ").append(info.getName()).append("\n");
            stringBuilder.append("Thời gian tạo: ").append(startDate).append("\n");
            stringBuilder.append("Số đơn hàng thành công: ").append(response.getData().getOrders().size() - cancelOrder).append("(").append(lineItems.size()).append(" sản phẩm)").append("\n");
            stringBuilder.append("Số đơn hàng bị cancel: ").append(cancelItem).append("(").append(cancelItem).append(" sản phẩm)").append("\n");
            stringBuilder.append("====================================>>Số lượng đơn theo mỗi loại<<====================================").append("\n");
            for (String key : groupPackageByItem.keySet()) {
                String name = skuInfo.get(key) == null ? key : skuInfo.get(key);
                stringBuilder.append("donCoNhieuHon1SP".equals(key) ? "Đơn có nhiều sản phẩm" : name).append(": ").append(groupPackageByItem.get(key).size()).append("\n");
            }
            stringBuilder.append("====================================>>Số lượng cụ thể từng sku<<====================================").append("\n");
            for (String key : count.keySet()) {
                if (!skuInfo.containsKey(key)) {
                    stringBuilder.append("Không có key: ").append(key).append(" trong danh sách sku!\n");
                }
                stringBuilder.append(skuInfo.get(key) == null ? key : skuInfo.get(key)).append(": ").append(count.get(key)).append("\n");
            }

            if (!productSkuIds.isEmpty()) {
                stringBuilder.append("====================================>>!<<====================================").append("\n");
                stringBuilder.append("Chỉ in các đơn hàng chứa các sản phẩm sau: \n");
                productSkuIds.forEach(e -> {
                    stringBuilder.append("\t- ").append("donCoNhieuHon1SP".equals(e) ? "Đơn có nhiều sản phẩm" : (skuInfo.get(e) == null ? e : skuInfo.get(e))).append(": ").append(groupPackageByItem.get(e).size()).append("\n");
                });
            }

            if (!donHangDoiSizeMauCorrect.isEmpty()) {
                stringBuilder.append("====================================>>!<<====================================").append("\n");
                stringBuilder.append("Các đơn hàng đã thay đổi thông tin: \n");
                for (FormResult formResult : donHangDoiSizeMauCorrect.values()) {
                    stringBuilder.append(formResult.getOrderId()).append(": ").append(formResult.getProductName()).append(", ").append(formResult.getOriginalSkuName()).append(" -> ").append(formResult.getChangeSkuName()).append("\n");
                }
            }

            Arrays.stream(new File("/Users/dle/IdeaProjects/tiktok/temp/").listFiles()).forEach(File::delete);
//            Arrays.stream(new File("/Users/dle/IdeaProjects/tiktok/result/").listFiles()).forEach(File::delete);
            GoogleDriveUtils.createGoogleFile(shopFolder.getId(), "text/plain", "Info.txt", stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            FileUtils.writeStringToFile(new File(resultFolderPath + "Info.txt"), stringBuilder.toString(), "UTF-8");

        }

        return Response.ok(stringBuilder.toString()).build();
    }

    @POST
    @Path("/count/all")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response countAll(List<List<String>> mappings,
                             @QueryParam("code") List<String> shopCode,
                             @QueryParam("status") @DefaultValue("AWAITING_COLLECTION") String status,
                             @QueryParam("countLE") @DefaultValue("0") Integer countLE,
                             @QueryParam("pageSize") @DefaultValue("100") String pageSize,
                             @QueryParam("productSkuIds") List<String> productSkuIds) throws IOException {

        List<ShopInfo> list = shopRepository.find("code in ?1", shopCode).list();
        //Optional<ShopInfo> shopInfo = shopRepository.find("code = ?1", shopCode).singleResultOptional();
        if (list.isEmpty()) {
            return Response.serverError().build();
        }

        return Response.ok(objectMapper.writeValueAsString(list)).build();
    }

    static class FormResult {
        String orderId;
        String shopName;
        String shopCode;
        String productName;
        String productId;
        String originalSkuName;
        String originalSkuId;
        String changeSkuName;
        String changeSkuId;

        public FormResult() {
        }

        public FormResult(FormResponse response) {
            Collection<Answer> answers = response.getAnswers().values();
            for (Answer answer : answers) {
                TextAnswer textAnswer = answer.getTextAnswers().getAnswers().get(0);
                if (textAnswer != null) {
                    String answerValue = textAnswer.getValue();
                    String codeOrId = StringUtils.substringBetween(answerValue, "[", "]");
                    String name = answerValue.substring(0, !answerValue.contains("[") ? 0 : answerValue.indexOf("[")).trim();
                    if (answerValue.indexOf("<SHOP>") > 0) {
                        this.shopCode = codeOrId;
                        this.shopName = name;
                    } else if (answerValue.indexOf("<PRODUCT>") > 0) {
                        this.productId = codeOrId;
                        this.productName = name;
                    } else if (answerValue.indexOf("<ORIGINAL>") > 0) {
                        this.originalSkuId = codeOrId;
                        this.originalSkuName = name;
                    } else if (answerValue.indexOf("<CHANGE>") > 0) {
                        this.changeSkuId = codeOrId;
                        this.changeSkuName = name;
                    } else {
                        this.orderId = answerValue;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "FormResult{" +
                    "orderId='" + orderId + '\'' +
                    ", shopName='" + shopName + '\'' +
                    ", shopCode='" + shopCode + '\'' +
                    ", productName='" + productName + '\'' +
                    ", productId='" + productId + '\'' +
                    ", originalSkuName='" + originalSkuName + '\'' +
                    ", originalSkuId='" + originalSkuId + '\'' +
                    ", chaneSkuName='" + changeSkuName + '\'' +
                    ", changeSkuId='" + changeSkuId + '\'' +
                    '}';
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getShopCode() {
            return shopCode;
        }

        public void setShopCode(String shopCode) {
            this.shopCode = shopCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getOriginalSkuName() {
            return originalSkuName;
        }

        public void setOriginalSkuName(String originalSkuName) {
            this.originalSkuName = originalSkuName;
        }

        public String getOriginalSkuId() {
            return originalSkuId;
        }

        public void setOriginalSkuId(String originalSkuId) {
            this.originalSkuId = originalSkuId;
        }

        public String getChangeSkuName() {
            return changeSkuName;
        }

        public void setChangeSkuName(String changeSkuName) {
            this.changeSkuName = changeSkuName;
        }

        public String getChangeSkuId() {
            return changeSkuId;
        }

        public void setChangeSkuId(String changeSkuId) {
            this.changeSkuId = changeSkuId;
        }
    }

    static class TextLine {
        public List<TextPosition> textPositions = null;
        public String text = "";
    }

    static class myStripper extends PDFTextStripper {
        public myStripper() throws IOException {
        }

        @Override
        protected void startPage(PDPage page) throws IOException {
            startOfLine = true;
            super.startPage(page);
        }

        @Override
        protected void writeLineSeparator() throws IOException {
            startOfLine = true;
            super.writeLineSeparator();
        }

        @Override
        public String getText(PDDocument doc) throws IOException {
            lines = new ArrayList<TextLine>();
            return super.getText(doc);
        }

        @Override
        protected void writeWordSeparator() throws IOException {
            TextLine tmpline = null;

            tmpline = lines.get(lines.size() - 1);
            tmpline.text += getWordSeparator();

            super.writeWordSeparator();
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
            TextLine tmpline = null;

            if (startOfLine) {
                tmpline = new TextLine();
                tmpline.text = text;
                tmpline.textPositions = textPositions;
                lines.add(tmpline);
            } else {
                tmpline = lines.get(lines.size() - 1);
                tmpline.text += text;
                tmpline.textPositions.addAll(textPositions);
            }

            if (startOfLine) {
                startOfLine = false;
            }
            super.writeString(text, textPositions);
        }

        boolean startOfLine = true;
        public ArrayList<TextLine> lines = null;
    }

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response test(@QueryParam("filePath") String filePath) throws IOException {
        List<String> orderIds = FileUtils.readLines(new File(filePath));
        return Response.ok().build();
    }

}
