package com.yalovchuk.socket;

import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class Main {

  @Test
  public void runServerSocketAndMultipleClients() {
    try (Server server = new Server(64020)) {

      new Thread(server::start).start();

      Thread[] threads = {
          new Thread(() -> {
            try (Client client = new Client("localhost", 64020)) {
              Assert.assertEquals(client.sendMessage("1+2"), "3.0");
              Assert.assertEquals(client.sendMessage("2*4"), "8.0");
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }),
          new Thread(() -> {
            try (Client client = new Client("localhost", 64020)) {
              Assert.assertEquals(client.sendMessage("2+2*2"), "6.0");
              Assert.assertEquals(client.sendMessage("(2+3)*(2+4)/2"), "15.0");
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          })};

      Arrays.stream(threads).forEach(Thread::start);
      for (Thread thread : threads) {
        thread.join();
      }
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
