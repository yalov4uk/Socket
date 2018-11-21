package com.yalovchuk.socket.server;

public class ServerMain {

  public static void main(String[] args) throws Exception {
    int port = args.length > 1 ? Integer.parseInt(args[1]) : 50000;
    try (Server server = new Server(port)) {
      server.start();
    }
  }
}
