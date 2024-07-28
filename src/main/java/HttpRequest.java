import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequest {
  private RequestType type;

  private String url;

  private Map<String, String> headers;

  private String body;

  public HttpRequest(
      RequestType requestType, String requestUrlPath, Map<String, String> headers, String body) {
    this.type = requestType;
    this.url = requestUrlPath;
    this.headers = headers;
    this.body = body;
  }

  public HttpRequest(BufferedReader reader) throws IOException {
    String initialLine = reader.readLine();
    if (initialLine == null || initialLine.isEmpty()) {
      throw new IOException("Received an empty request");
    }

    StringTokenizer tokenizer = new StringTokenizer(initialLine);
    type = RequestType.valueOf(tokenizer.nextToken());
    url = tokenizer.nextToken();

    headers = new HashMap();
    String line;
    while ((line = reader.readLine()).length() != 0 && !line.isEmpty()) {
      String[] header = line.split(":");
      headers.put(header[0].trim(), header[1].trim());
    }

    if (headers.containsKey("Content-Length")) {
      int contentLength = Integer.parseInt(headers.get("Content-Length"));
      char[] bodyChars = new char[contentLength];
      int read = reader.read(bodyChars, 0, contentLength);
      if (read != contentLength) {
        throw new IOException("Content-Length and actual body length do not match");
      }
      body = new String(bodyChars);
    } else {
      body = "";
    }
  }

  public RequestType getType() {
    return type;
  }

  public void setType(RequestType type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public enum RequestType {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH
  }
}
