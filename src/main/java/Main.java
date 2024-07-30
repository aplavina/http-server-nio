import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    HttpRequestHandler handler = new SimpleHandler();
    NioHttpServer server = new NioHttpServer(handler, 4221);
    server.start();
  }
}
