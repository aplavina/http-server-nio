import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleHandler implements HttpRequestHandler {
  private final String directoryPath;

  public SimpleHandler(String directoryPath) {
    this.directoryPath = directoryPath;
  }

  @Override
  public String handle(String requestStr) {
    HttpRequest request = new HttpRequest(requestStr);
    HttpResponse httpResponse = new HttpResponse();

    if (request.getUrl().equals("/")) {
      httpResponse.setStatus(200);
      httpResponse.setStatusDescr("OK");
    } else if (request.getUrl().equals("/user-agent")) {
      httpResponse.setStatus(200);
      httpResponse.setStatusDescr("OK");
      String body = request.getHeaders().get("User-Agent");
      httpResponse.setBody(body);
      httpResponse.addHeader("Content-Type", "text/plain");
      httpResponse.addHeader("Content-Length", "" + body.length());
    } else if (request.getUrl().startsWith("/echo/")) {
      httpResponse.setStatus(200);
      httpResponse.setStatusDescr("OK");
      final String body = request.getUrl().split("/echo/")[1];
      httpResponse.setBody(body);
      httpResponse.addHeader("Content-Type", "text/plain");
      httpResponse.addHeader("Content-Length", "" + body.length());
    } else if (request.getUrl().startsWith("/files/")) {
      try {
        httpResponse.setStatus(200);
        httpResponse.setStatusDescr("OK");
        String file = directoryPath + "/" + request.getUrl().split("/files/")[1];
        String body = Files.readString(Paths.get(file), StandardCharsets.UTF_8);
        httpResponse.setBody(body);
        httpResponse.addHeader("Content-Type", "application/octet-stream");
        httpResponse.addHeader("Content-Length", "" + body.length());
      } catch (IOException ex) {
        ex.printStackTrace();
        httpResponse.setStatus(404);
        httpResponse.setStatusDescr("Not Found");
      }
    } else {
      httpResponse.setStatus(404);
      httpResponse.setStatusDescr("Not Found");
    }
    return httpResponse.getResponseString();
  }
}
