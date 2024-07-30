public class Main {
  public static void main(String[] args) {
    String directoryPath = null;

    for (int i = 0; i < args.length; i++) {
      if ("--directory".equals(args[i]) && i + 1 < args.length) {
        directoryPath = args[i + 1];
        break;
      }
    }
    HttpRequestHandler handler = new SimpleHandler(directoryPath == null ? "" : directoryPath);
    NioHttpServer server = new NioHttpServer(handler, 4221);
    server.start();
  }
}
