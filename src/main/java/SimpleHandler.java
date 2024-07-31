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
  public byte[] handle(String requestStr) {
    HttpRequest request = new HttpRequest(requestStr);
    HttpResponse response = new HttpResponse();

    if (request.getUrl().equals("/")) {
      response.setStatus(200);
      response.setStatusDescr("OK");
    } else if (request.getUrl().equals("/user-agent")) {
      response.setStatus(200);
      response.setStatusDescr("OK");
      String body = request.getHeaders().get("User-Agent");
      response.setBody(body.getBytes());
      response.addHeader("Content-Type", "text/plain");
      response.addHeader("Content-Length", "" + body.length());
    } else if (request.getUrl().startsWith("/echo/")) {

      try {
        response.setStatus(200);
        response.setStatusDescr("OK");
        final String body = request.getUrl().split("/echo/")[1];
        response.setBody(body.getBytes());
        handleCompression(request, response);
        response.addHeader("Content-Type", "text/plain");
        response.addHeader("Content-Length", "" + response.getBody().length);
      } catch (IOException ex) {
        response.setStatus(500);
        response.setStatusDescr("Internal Server Error");
        ex.printStackTrace();
      }
    } else if (request.getType() == HttpRequest.RequestType.GET
        && request.getUrl().startsWith("/files/")) {
      try {
        response.setStatus(200);
        response.setStatusDescr("OK");
        String file = directoryPath + "/" + request.getUrl().split("/files/")[1];
        String body = Files.readString(Paths.get(file), StandardCharsets.UTF_8);
        response.setBody(body.getBytes());
        response.addHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-Length", "" + body.length());
      } catch (IOException ex) {
        ex.printStackTrace();
        response.setStatus(404);
        response.setStatusDescr("Not Found");
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
            response.setStatus(201);
            response.setStatusDescr("Created");
          }
        } else {
          response.setStatus(500);
          response.setStatusDescr("Internal Server Error");
        }
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    } else {
      response.setStatus(404);
      response.setStatusDescr("Not Found");
    }
    return response.getResponseByteArray();
  }

  private void handleCompression(HttpRequest request, HttpResponse response) throws IOException {
    if (request.getHeaders().get("Accept-Encoding") != null) {
      String[] encodings = request.getHeaders().get("Accept-Encoding").split(", ");
      for (String encoding : encodings) {
        if (encoding.equals("gzip")) {
          response.addHeader("Content-Encoding", "gzip");
          response.compressBody();
        }
      }
    }
  }
}
