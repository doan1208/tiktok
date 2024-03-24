package com.dle.multi.thread;

import com.dle.Orders;
import com.dle.bean.database.ShopInfo;
import com.dle.bean.order.Order;
import com.dle.bean.order.Package;
import com.dle.bean.shipping.ShippingDocumentResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ClientMultiQuery implements Runnable{

    CloseableHttpClient httpClient;
    HttpGet httpget;

    String id;

    public ClientMultiQuery(CloseableHttpClient httpClient, HttpGet httpget) {
        this.httpClient = httpClient;
        this.httpget = httpget;
        this.id = "1";
    }

    @Override
    public void run() {
        try{
            //Executing the request
            CloseableHttpResponse httpresponse = httpClient.execute(httpget);

            //Displaying the status of the request.
            System.out.println("status of thread "+httpget.getURI().getHost()+":"+httpresponse.getStatusLine());

            //Retrieving the HttpEntity and displaying the no.of bytes read
//            HttpEntity entity = httpresponse.getEntity();
//            if (entity != null) {
//                System.out.println("Bytes read by thread thread "+id+":
//                        "+EntityUtils.toByteArray(entity).length);
//            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //Creating the Client Connection Pool Manager by instantiating the PoolingHttpClientConnectionManager class.
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

        //Set the maximum number of connections in the pool
        connManager.setMaxTotal(100);

        //Create a ClientBuilder Object by setting the connection manager
        HttpClientBuilder clientbuilder = HttpClients.custom().setConnectionManager(connManager);

        //Build the CloseableHttpClient object using the build() method.
        CloseableHttpClient httpclient = clientbuilder.build();

        //Creating the HttpGet requests
        HttpGet httpget1 = new HttpGet("https://www.tutorialspoint.com/");
        HttpGet httpget2 = new HttpGet("http://www.google.com/");
        HttpGet httpget3 = new HttpGet("https://www.qries.com/");
        HttpGet httpget4 = new HttpGet("https://in.yahoo.com/");
        List<HttpGet> list = new ArrayList<>();
        list.add(httpget1);
        list.add(httpget2);
        list.add(httpget3);
        list.add(httpget4);

        ExecutorService executor = createExecutorService(10, "threadName");
        List<Future> futures = new ArrayList<>();
//        Future<T> future = executor.submit(new Runnable() {
//            public void run() {
//                System.out.println("Asynchronous task");
//            }
//        }, T.class);

        try {
            for (HttpGet httpGet : list) {
                Future future = executor.submit(new ClientMultiQuery(httpclient, httpGet));
                futures.add(future); // Trả về đối tượng T mà bạn truyền vào, dựa vào đây xác đây nhiệm đã hòan tất
            }
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
        System.out.println();
    }


//    void test(List<Order> orders, CloseableHttpClient httpClient, ShopInfo info){
//        Map<String, String> documentParams = new HashMap<>();
//        documentParams.put("document_type", "SHIPPING_LABEL_AND_PACKING_SLIP");
//        documentParams.put("document_size", "A6");
//
//        CountDownLatch latch = new CountDownLatch(orders.size());
//        ExecutorService taskExecutor = Executors.newFixedThreadPool(10);
//        for (Order order : orders) {
//            List<String> packageIds = order.getPackages().stream().map(Package::getId).collect(Collectors.toList());
//            for (String packageId : packageIds) {
//                Callable<String> callable = () -> {
//                    ShippingDocumentResponse documentResponse = Orders.getShippingDocument(info, documentParams, packageId);
//                    return null;
//                }
//            }
//        }
//    }

    private static ExecutorService createExecutorService(int parallelismDegree, final String threadName) {
        return Executors.newFixedThreadPool(parallelismDegree, runnable -> {
            Thread t = new Thread(runnable);
            t.setPriority(Thread.MIN_PRIORITY);
            t.setName(threadName);
            return t;
        });
    }
}
