public class SimpleHandler implements HttpRequestHandler {

  @Override
  public String handle(String requestStr) {
    HttpRequest request = new HttpRequest(requestStr);
    HttpResponse httpResponse = new HttpResponse();
    httpResponse.setStatus(200);
    httpResponse.setStatusDescr("OK");
    String body = request.getHeaders().get("User-Agent");
    httpResponse.setBody(body);
    httpResponse.addHeader("Content-Type", "text/plain");
    httpResponse.addHeader("Content-Length", "" + body.length());
    return httpResponse.getResponseString();
  }
}
