package edu.demo.scfs.service;

public interface EmailApiClient {
    void send(String to, String subject, String text, String from);
}
