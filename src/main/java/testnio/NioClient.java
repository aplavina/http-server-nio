package testnio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class NioClient {
  public void start(final int portNumber, final Scanner scanner) {
    try (SocketChannel serverChannel = SocketChannel.open()) {
      serverChannel.connect(new InetSocketAddress(portNumber));
      System.out.println("Connection established");
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      while (true) {
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("quit")) {
          break;
        }
        String fileContent = Files.readString(Paths.get(line), StandardCharsets.UTF_8);
        buffer.clear().put(fileContent.getBytes()).flip();
        while (buffer.hasRemaining()) {
          serverChannel.write(buffer);
        }
        buffer.clear();
        int bytesRead = serverChannel.read(buffer);
        if (bytesRead > 0) {
          buffer.flip();
          String data = new String(buffer.array(), buffer.position(), bytesRead);
          System.out.println("Server: " + data);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new NioClient().start(12345, new Scanner(System.in));
  }
}
