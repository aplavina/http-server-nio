public class SimpleHandler implements HttpRequestHandler {

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
    } else {
      httpResponse.setStatus(404);
      httpResponse.setStatusDescr("Not Found");
    }
    return httpResponse.getResponseString();
  }
}
