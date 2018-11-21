package com.yalovchuk.socket.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Closeable {

  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  private final ServerSocket serverSocket;
  private final AtomicInteger id = new AtomicInteger(0);
  private final Map<Integer, Socket> clients = new ConcurrentHashMap<>();

  public Server(int port) throws IOException {
    serverSocket = new ServerSocket(port);
  }

  public void start() {
    try {
      while (!serverSocket.isClosed()) {
        Socket socket = serverSocket.accept();
        int key = id.incrementAndGet();
        clients.put(key, socket);
        new Thread(new ClientHandler(socket, key, clients)).start();
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
