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

      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);

      Socket socket = serverSocket.accept(); // Wait for connection from client.
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
          httpResponse.addHeader("Content-Length", "" + str.length());
          writer.write(httpResponse.getResponseString());
          writer.flush();

          System.out.println(httpResponse.getResponseString());
        } else {
          writer.write("HTTP/1.1 404 Not Found\r\n\r\n");
        }
        writer.flush();
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
