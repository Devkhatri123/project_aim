package com.newsApi.newsApi.exception.userexception;

public class EmailAlreadyTaken extends RuntimeException {
  public EmailAlreadyTaken(String message) {
    super(message);
  }
}
