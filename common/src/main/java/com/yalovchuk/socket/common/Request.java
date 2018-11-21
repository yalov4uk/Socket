package com.yalovchuk.socket.common;


public class Request {

  private Command command;
  private String request;

  public Command getCommand() {
    return command;
  }

  public void setCommand(Command command) {
    this.command = command;
  }

  public String getRequest() {
    return request;
  }

  public void setRequest(String request) {
    this.request = request;
  }

  @Override
  public String toString() {
    return "Request{" +
        "command=" + command +
        ", request='" + request + '\'' +
        '}';
  }
}
