package com.seminar.rust;

public class User {
    private String username;
    private long pin;
    private double zarada;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPin() {
        return pin;
    }

    public void setPin(long pin) {
        this.pin = pin;
    }

    public double getZarada() {
        return zarada;
    }

    public void setZarada(double zarada) {
        this.zarada = zarada;
    }

    public User() {
        super();
    }

    public User(String username, long pin, double zarada) {
        this.username = username;
        this.pin = pin;
        this.zarada = zarada;
    }
}
