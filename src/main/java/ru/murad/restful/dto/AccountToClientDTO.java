package ru.murad.restful.dto;

public class AccountToClientDTO {
    private String name;
    private double balance;

    public AccountToClientDTO() {
    }

    public AccountToClientDTO(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
