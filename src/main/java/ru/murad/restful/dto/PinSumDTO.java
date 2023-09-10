package ru.murad.restful.dto;

public class PinSumDTO {
    private int pin;
    private double sum;

    public PinSumDTO() {
    }

    public PinSumDTO(int pin, double sum) {
        this.pin = pin;
        this.sum = sum;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
