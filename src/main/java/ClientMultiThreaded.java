import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class ClientMultiThreaded extends Thread {
    CloseableHttpClient httpClient;
    HttpGet httpget;
    int id;

    public ClientMultiThreaded(CloseableHttpClient httpClient, HttpGet httpget,
                               int id) {
        this.httpClient = httpClient;
        this.httpget = httpget;
        this.id = id;
    }
    @Override
    public void run() {
        try{
            //Executing the request
            CloseableHttpResponse httpresponse = httpClient.execute(httpget);

            //Displaying the status of the request.
            System.out.println("status of thread "+id+":"+httpresponse.getStatusLine());

            //Retrieving the HttpEntity and displaying the no.of bytes read
            HttpEntity entity = httpresponse.getEntity();

        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
