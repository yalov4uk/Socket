package com.yalovchuk.socket;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Closeable {

  private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

  private final Socket clientSocket;
  private final BufferedReader in;
  private final PrintWriter out;

  public Client(String ip, int port) throws IOException {
    clientSocket = new Socket(ip, port);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    out = new PrintWriter(clientSocket.getOutputStream(), true);
  }

  public String sendMessage(String request) {
    out.println(request);
    try {
      return in.readLine();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, String.format("Error while reading response ip = %s, port = %s",
          clientSocket.getInetAddress(), clientSocket.getPort()));
      return null;
    }
  }

  @Override
  public void close() throws IOException {
    clientSocket.close();
    in.close();
    out.close();
    LOGGER.log(Level.INFO, String.format("Client socket ip = %s, port = %s closed",
        clientSocket.getInetAddress(), clientSocket.getPort()));
  }
}
