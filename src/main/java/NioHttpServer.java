import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NioHttpServer {
  private Set<SocketChannel> clients = new HashSet();
  private Map<SocketChannel, StringBuilder> clientsMessages = new HashMap();
  private Map<SocketChannel, Integer> messagesLength = new HashMap();
  private final ByteBuffer buffer = ByteBuffer.allocate(1024);

  private final HttpRequestHandler requestHandler;
  private final int PORT;

  public NioHttpServer(HttpRequestHandler requestHandler, final int port) {
    this.requestHandler = requestHandler;
    this.PORT = port;
  }

  public void start() {
    try (ServerSocketChannel ssc = ServerSocketChannel.open();
        Selector selector = Selector.open()) {
      ssc.configureBlocking(false);
      ssc.bind(new InetSocketAddress(PORT));
      ssc.register(selector, SelectionKey.OP_ACCEPT);
      while (true) {
        if (selector.select() == 0) {
          continue;
        }
        for (SelectionKey key : selector.selectedKeys()) {
          if (key.isAcceptable()) {
            if (key.channel() instanceof ServerSocketChannel channel) {
              registerClient(channel, selector);
            } else {
              throw new RuntimeException("Unknown channel");
            }
          }
          if (key.isReadable()) {
            if (key.channel() instanceof SocketChannel client) {
              processClientSentData(client);
            } else {
              throw new RuntimeException("Unknown channel");
            }
          }
        }
        selector.selectedKeys().clear();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      for (SocketChannel client : clients) {
        try (client) {
          clients.remove(client);
          clientsMessages.remove(client);
          messagesLength.remove(client);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  private void processClientSentData(SocketChannel client) throws IOException {
    try {
      int bytesRead = client.read(buffer);
      if (bytesRead < 0) {
        handleDisconnect(client);
        return;
      }
      buffer.flip();
      String chunk = new String(buffer.array(), buffer.position(), bytesRead);
      if (clientsMessages.containsKey(client)) {
        clientsMessages.get(client).append(chunk);
      } else {
        clientsMessages.put(client, new StringBuilder(chunk));
      }
      if (sentHeaders(client)) {
        processHeaders(client);
      }
      if (sentFullMessage(client)) {
        processFullMessage(client);
      }
    } catch (SocketException ex) {
      handleDisconnect(client);
    } finally {
      buffer.clear();
    }
  }

  private void handleDisconnect(SocketChannel client) throws IOException {
    try (client) {
      clients.remove(client);
      clientsMessages.remove(client);
      messagesLength.remove(client);
      System.out.println(
          "Client disconnected: "
              + client.socket().getInetAddress()
              + ":"
              + client.socket().getPort());
    }
  }

  private boolean sentHeaders(SocketChannel client) {
    return !messagesLength.containsKey(client)
        && (clientsMessages.get(client).toString().contains("\r\n\r\n")
            || clientsMessages.get(client).toString().contains("\n\n"));
  }

  private void processHeaders(SocketChannel client) {
    String headers = clientsMessages.get(client).toString();
    int contentLength = getContentLength(headers);
    String newline = headers.contains("\n\n") ? "\n\n" : "\r\n\r\n";
    messagesLength.put(
        client, headers.split(newline)[0].length() + newline.length() + contentLength);
    System.out.println(
        "Length: " + (headers.split(newline)[0].length() + newline.length() + contentLength));
  }

  private boolean sentFullMessage(SocketChannel client) {
    return messagesLength.containsKey(client)
        && clientsMessages.get(client).length() >= messagesLength.get(client);
  }

  private void processFullMessage(SocketChannel client) throws IOException {
    String request = clientsMessages.get(client).toString();
    System.out.println("Full Message With Body: " + request);
    clientsMessages.remove(client);
    messagesLength.remove(client);
    String response = requestHandler.handle(request);
    byte[] responseBytes = response.getBytes();
    int offset = 0;
    buffer.clear();
    while (offset < responseBytes.length) {
      int chunkSize = Math.min(buffer.capacity(), responseBytes.length - offset);
      buffer.clear().put(responseBytes, offset, chunkSize).flip();
      while (buffer.hasRemaining()) {
        client.write(buffer);
      }
      buffer.clear();
      offset += chunkSize;
    }
    buffer.clear();
  }

  private int getContentLength(String data) {
    String newline = data.contains("\n\n") ? "\n" : "\r\n";
    String[] headers = data.split(newline);
    for (String header : headers) {
      if (header.startsWith("Content-Length:")) {
        return Integer.parseInt(header.substring(15).trim());
      }
    }
    return 0;
  }

  private void registerClient(ServerSocketChannel channel, Selector selector) throws IOException {
    SocketChannel client = channel.accept();
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_READ);
    clients.add(client);
    System.out.println(
        "Registered client " + client.socket().getInetAddress() + ":" + client.socket().getPort());
  }

  public static void main(String[] args) {
    new NioHttpServer(new SimpleHandler(), 4221).start();
  }
}
