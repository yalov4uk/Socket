package com.yalovchuk.socket.client;

import java.util.Scanner;

public class ClientMain {

  public static void main(String[] args) throws Exception {
    String host = args.length > 1 ? args[1] : "localhost";
    int port = args.length > 2 ? Integer.parseInt(args[2]) : 50000;
    Scanner scanner = new Scanner(System.in);
    try (Client client = new Client(host, port)) {
      String request;
      while (!"$".equals(request = scanner.nextLine())) {
        String response = client.sendMessage(request);
        System.out.println(response);
      }
    }
  }
}
