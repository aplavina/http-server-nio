public class SimpleHandler implements HttpRequestHandler {

  @Override
  public String handle(String request) {
    HttpResponse httpResponse = new HttpResponse();
    httpResponse.setStatus(200);
    httpResponse.setStatusDescr("OK");
    String body = "hello worlds hi";
    httpResponse.setBody(body);
    httpResponse.addHeader("Content-Type", "text/plain");
    httpResponse.addHeader("Content-Length", "" + body.length());
    return httpResponse.getResponseString();
  }
}
