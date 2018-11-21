package com.yalovchuk.socket.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalovchuk.socket.common.Request;
import com.yalovchuk.socket.common.Response;
import com.yalovchuk.socket.server.calculator.Calculator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final Calculator calculator = new Calculator();
  private final Socket clientSocket;
  private final int id;
  private final Map<Integer, Socket> clients;

  public ClientHandler(Socket clientSocket, int id, Map<Integer, Socket> clients) {
    this.clientSocket = clientSocket;
    this.id = id;
    this.clients = clients;
  }

  @Override
  public void run() {
    String req;
    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

      while ((req = in.readLine()) != null) {
        Request request = OBJECT_MAPPER.readValue(req, Request.class);
        LOGGER.log(Level.INFO, String.format("id = %s, ip = %s, port = %s, request = %s",
            id, clientSocket.getInetAddress(), clientSocket.getPort(), request));

        Response response = new Response();
        boolean closeFlag = false;
        switch (request.getCommand()) {
          case CALCULATE:
            response.setResponse(calculator.evaluateExpression(request.getRequest()));
            break;
          case GET_CLIENTS:
            response.setResponse(new ArrayList<>(clients.keySet()).toString());
            break;
          case GREET_WITH_ANOTHER_CLIENT:
            Request greetToAnotherClient = new Request();
            Socket anotherClientSocket = clients.get(Integer.parseInt(request.getRequest()));
            try (PrintWriter outToAnotherClient
                = new PrintWriter(anotherClientSocket.getOutputStream(), true)) {
              outToAnotherClient.write(OBJECT_MAPPER.writeValueAsString(greetToAnotherClient));
            }
            break;
          case BYE:
            response.setResponse("Bye");
            clients.remove(id);
            closeFlag = true;
            break;
        }

        LOGGER.log(Level.INFO, String.format("ip = %s, port = %s, response = %s",
            clientSocket.getInetAddress(), clientSocket.getPort(), response));
        out.println(OBJECT_MAPPER.writeValueAsString(response));
        if (closeFlag) {
          break;
        }
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
