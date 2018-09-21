package com.yalovchuk.socket;

import com.yalovchuk.socket.calculator.Calculator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
  private static final Calculator calculator = new Calculator();

  private Socket clientSocket;

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    String request;
    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream())); PrintWriter out = new PrintWriter(
        clientSocket.getOutputStream(), true)) {

      while ((request = in.readLine()) != null) {
        LOGGER.log(Level.INFO, String.format("ip = %s, port = %s, request = %s",
            clientSocket.getInetAddress(), clientSocket.getPort(), request));
        if ("$".equals(request)) {
          LOGGER.log(Level.INFO, String.format("ip = %s:%s, bye", clientSocket.getInetAddress(),
              clientSocket.getPort()));
          break;
        }
        String response = calculator.evaluateExpression(request);
        LOGGER.log(Level.INFO, String.format("ip = %s, port = %s, response = %s",
            clientSocket.getInetAddress(), clientSocket.getPort(), response));
        out.println(response);
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error while handling request", e);
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Error while closing client socket", e);
      }
    }
  }
}
