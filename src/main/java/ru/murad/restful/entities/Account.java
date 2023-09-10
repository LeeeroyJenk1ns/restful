package ru.murad.restful.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_number")
    private long accountNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "pin")
    private int pin;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    @Transient
    //@Formula(value = "(SELECT sum(amount) FROM transaction WHERE account_number = account.account_number)")
    private double balance;

    public Account() {

    }

    public Account(long accountNumber, String name, int pin, List<Transaction> transactions) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.pin = pin;
        this.transactions = transactions;
    }

    @PostLoad
    private void initBalance() {
        this.balance = getTransactions()
                .stream()
                .map(Transaction::getAmount)
                .reduce(0.0, Double::sum);
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public double getBalance() { return balance;}

    public void setBalance() {
        if (transactions != null) {
            initBalance();
        }
    }
}
