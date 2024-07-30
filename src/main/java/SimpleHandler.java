import java.io.File;
import java.io.FileWriter;
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
    } else if (request.getType() == HttpRequest.RequestType.GET
        && request.getUrl().startsWith("/files/")) {
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
    } else if (request.getType() == HttpRequest.RequestType.POST
        && request.getUrl().startsWith("/files/")) {
      String path = directoryPath + "/" + request.getUrl().split("/files/")[1];
      String body = request.getBody();
      try {
        File file = new File(path);
        if (file.createNewFile()) {
          try (FileWriter myWriter = new FileWriter(path); ) {
            myWriter.write(body);
            httpResponse.setStatus(201);
            httpResponse.setStatusDescr("Created");
          }
        } else {
          httpResponse.setStatus(500);
          httpResponse.setStatusDescr("Internal Server Error");
        }
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    } else {
      httpResponse.setStatus(404);
      httpResponse.setStatusDescr("Not Found");
    }
    return httpResponse.getResponseString();
  }
}
