import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    System.out.println("Logs from your program will appear here!");
    try {
      ServerSocket serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);

      Socket socket = serverSocket.accept();
      System.out.println("accepted new connection");
      try (BufferedReader reader =
              new BufferedReader(new InputStreamReader(socket.getInputStream()));
          BufferedWriter writer =
              new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); ) {
        HttpRequest httpRequest = new HttpRequest(reader);
        String url = httpRequest.getUrl();
        HttpResponse httpResponse = new HttpResponse();
        if (url.startsWith("/echo")) {
          httpResponse.setStatus(200);
          httpResponse.setStatusDescr("OK");
          String prefix = "/echo";
          String str = url.substring(prefix.length() + 1);
          httpResponse.setBody(str);
          httpResponse.addHeader("Content-Type", "text/plain");
        } else if (url.equals("/")) {
          httpResponse.setStatus(200);
          httpResponse.setStatusDescr("OK");
        } else if (url.equals("/user-agent")) {
          if (httpRequest.getHeaders().containsKey("User-Agent")) {
            httpResponse.setStatus(200);
            httpResponse.setStatusDescr("OK");
            httpResponse.setBody(httpRequest.getHeaders().get("User-Agent"));
          } else {
            httpResponse.setStatus(400);
            httpResponse.setStatusDescr("User-Agent header must be specified");
          }
        } else {
          httpResponse.setStatus(404);
          httpResponse.setStatusDescr("Not Found");
        }
        writer.write(httpResponse.getResponseString());
        writer.flush();
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
