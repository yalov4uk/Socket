package com.yalovchuk.socket.client;

import java.util.Scanner;

public class ClientMain {

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws Exception {
    String host = args.length > 1 ? args[1] : "localhost";
    int port = args.length > 2 ? Integer.parseInt(args[2]) : 50000;
    try (Client client = new Client(host, port)) {
      System.out.println("1. Write; 2. Read; 3. Exit.");
      int option;
      while ((option = Integer.parseInt(scanner.nextLine())) != 3) {
        switch (option) {
          case 1:
            client.write(scanner.nextLine());
            break;
          case 2:
            System.out.println(client.read());
            break;
          default:
            throw new IllegalArgumentException();
        }
        System.out.println("***");
      }
    }
  }
}
