package com.example.demo;

public class Greeting{

    private String message;

    public Greeting() {
        super();
    }

    public Greeting(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}