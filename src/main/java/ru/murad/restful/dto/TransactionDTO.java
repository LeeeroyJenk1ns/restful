package ru.murad.restful.dto;

import java.util.Date;

public class TransactionDTO {
    private String type;
    private double amount;
    private Date createdAt;

    public TransactionDTO() {}
    public TransactionDTO(String type, double amount, Date createdAt) {
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
