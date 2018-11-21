package com.yalovchuk.socket.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Closeable {

  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  private final ServerSocket serverSocket;

  public Server(int port) throws IOException {
    serverSocket = new ServerSocket(port);
  }

  public void start() {
    try {
      while (!serverSocket.isClosed()) {
        new Thread(new ClientHandler(serverSocket.accept())).start();
      }
    } catch (IOException e) {
      LOGGER.log(Level.INFO, "Server socket closed");
    }
  }

  @Override
  public void close() throws IOException {
    serverSocket.close();
  }
}
