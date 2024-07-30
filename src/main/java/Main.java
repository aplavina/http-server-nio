import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    NioHttpServer server =
        new NioHttpServer(
            s ->
                "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: 19\r\n\r\nraspberry/pineapple",
            4221);
    server.start();
  }
}
