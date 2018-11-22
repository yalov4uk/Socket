package com.yalovchuk.socket.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalovchuk.socket.common.Command;
import com.yalovchuk.socket.common.Request;
import com.yalovchuk.socket.common.Response;
import com.yalovchuk.socket.server.calculator.Calculator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Calculator CALCULATOR = new Calculator();

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
    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream())); PrintWriter out = new PrintWriter(
        clientSocket.getOutputStream(), true)) {
      String input, output;
      while ((input = in.readLine()) != null) {
        LOGGER.log(Level.INFO, String
            .format("id = %s, ip = %s, port = %s, input = %s", id, clientSocket.getInetAddress(),
                clientSocket.getPort(), input));
        Request request = OBJECT_MAPPER.readValue(input, Request.class);
        Response response;
        switch (request.getCommand()) {
          case CALCULATE:
            response = calculate(request);
            break;
          case CLIENTS:
            response = clients(request);
            break;
          case GREET:
            response = greet(request);
            break;
          default:
            throw new IllegalStateException();
        }
        output = OBJECT_MAPPER.writeValueAsString(response);
        LOGGER.log(Level.INFO, String
            .format("id = %s, ip = %s, port = %s, output = %s", id, clientSocket.getInetAddress(),
                clientSocket.getPort(), output));
        out.println(output);
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, String
          .format("Client error id = %s, ip = %s, port = %s", id, clientSocket.getInetAddress(),
              clientSocket.getPort()), e);
    } finally {
      LOGGER.log(Level.WARNING, String
          .format("Remove client id = %s, ip = %s, port = %s", id, clientSocket.getInetAddress(),
              clientSocket.getPort()));
      clients.remove(id);
      try {
        clientSocket.close();
      } catch (IOException ignored) {
      }
    }
  }

  private Response calculate(Request request) {
    Response response = new Response();
    response.setResponse(CALCULATOR.evaluateExpression(request.getRequest()));
    return response;
  }

  private Response clients(Request request) {
    Response response = new Response();
    response.setResponse(clients.keySet()
        .stream()
        .filter(e -> e != id)
        .collect(Collectors.toList())
        .toString());
    return response;
  }

  private Response greet(Request request) throws IOException {
    Response response = new Response();
    response.setResponse(Command.GREET.name());
    Socket client = clients.get(Integer.parseInt(request.getRequest()));
    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
    out.println(OBJECT_MAPPER.writeValueAsString(response));
    return response;
  }
}
