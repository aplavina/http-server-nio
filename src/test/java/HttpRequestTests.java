import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.Assert;

public class HttpRequestTests {
  @Test
  public void simpleTest() throws Exception {
    InputStream is = HttpRequestTests.class.getResourceAsStream("requests/testReq1");
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    HttpRequest httpRequest = new HttpRequest(reader);

    HttpRequest.RequestType expectedType = HttpRequest.RequestType.POST;
    Assert.assertEquals(expectedType, httpRequest.getType());

    String expectedUrl = "/cgi-bin/process.cgi";
    Assert.assertEquals(expectedUrl, httpRequest.getUrl());

    Map<String, String> expectedHeaders = new HashMap();
    expectedHeaders.put("User-Agent", "Mozilla/4.0 (compatible; MSIE5.01; Windows NT)");
    expectedHeaders.put("Host", "www.tutorialspoint.com");
    expectedHeaders.put("Content-Type", "application/x-www-form-urlencoded");
    expectedHeaders.put("Content-Length", "49");
    expectedHeaders.put("Accept-Language", "en-us");
    expectedHeaders.put("Accept-Encoding", "gzip, deflate");
    expectedHeaders.put("Connection", "Keep-Alive");

    Map<String, String> actualHeaders = httpRequest.getHeaders();

    actualHeaders.forEach(
        (String key, String value) -> {
          Assert.assertTrue(actualHeaders.containsKey(key) && actualHeaders.get(key).equals(value));
        });

    String expectedBody = "licenseID=string&content=string&/paramsXML=string";
    Assert.assertEquals(expectedBody, httpRequest.getBody());
  }
}
