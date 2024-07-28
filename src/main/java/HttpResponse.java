import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
  private Integer status;
  private String statusDescr;
  private Map<String, String> headers = new HashMap();
  private String body;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getStatusDescr() {
    return statusDescr;
  }

  public void setStatusDescr(String statusDescr) {
    this.statusDescr = statusDescr;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public void addHeader(String key, String value) {
    headers.put(key, value);
  }

  public String getResponseString() {
    StringBuilder sb = new StringBuilder();
    if (status == null || statusDescr == null) {
      throw new IllegalStateException("Response status or description not set");
    }
    sb.append("HTTP/1.1 ");
    sb.append(status);
    sb.append(" ");
    sb.append(statusDescr);
    sb.append("\r\n");
    headers.forEach(
        (String key, String value) -> {
          sb.append(key);
          sb.append(": ");
          sb.append(value);
          sb.append("\r\n");
        });
    sb.append("\r\n");
    if (body != null) {
      sb.append(body);
    }
    return sb.toString();
  }
}
