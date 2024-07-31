import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class HttpResponse {
  private Integer status;
  private String statusDescr;
  private Map<String, String> headers = new HashMap();
  private byte[] body;

  private static final int COMPRESSION_BUFFER_SIZE = 1024;

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

  public byte[] getBody() {
    return body;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public void addHeader(String key, String value) {
    headers.put(key, value);
  }

  public void compressBody() throws IOException {
    if (body == null) {
      System.out.println("Trying to encode null body");
      return;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    gzip(new ByteArrayInputStream(body), os);
    byte[] compressed = os.toByteArray();
    this.body = compressed;
  }

  public static void gzip(InputStream is, OutputStream os) throws IOException {
    GZIPOutputStream gzipOs = new GZIPOutputStream(os);
    byte[] buffer = new byte[COMPRESSION_BUFFER_SIZE];
    int bytesRead = 0;
    while ((bytesRead = is.read(buffer)) > -1) {
      gzipOs.write(buffer, 0, bytesRead);
    }
    gzipOs.close();
  }

  public byte[] getResponseByteArray() {
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
      byte[] headersBytes = sb.toString().getBytes();
      byte[] res = new byte[headersBytes.length + body.length];
      for (int i = 0; i < headersBytes.length; ++i) {
        res[i] = headersBytes[i];
      }
      for (int i = headersBytes.length; i < res.length; ++i) {
        res[i] = body[i - headersBytes.length];
      }
      return res;
    }
    return sb.toString().getBytes();
  }
}
