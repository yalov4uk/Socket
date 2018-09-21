package com.yalovchuk.socket;

import java.io.IOException;
import org.junit.Test;

public class Main {

  @Test
  public void runServerSocketAndMultipleClients() {
    try (Server server = new Server(64020)) {
      new Thread(server::start).start();
      try (Client client1 = new Client("localhost", 64020);
          Client client2 = new Client("127.0.0.1", 64020)) {
        String response1 = client1.sendMessage("1+2");
        String response2 = client2.sendMessage("2*3");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
