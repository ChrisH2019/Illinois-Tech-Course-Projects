package com.weiconghong.stockwatch;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Stock implements Serializable, Comparable<Stock> {


    private String symbol;
    private String companyName;
    private double price;
    private double priceChange;
    private double changePercent;


    public Stock(String symbol, String companyName, double price, double priceChange, double changePercent) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
        this.priceChange = priceChange;
        this.changePercent = changePercent;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public String getCompanyName() {
        return companyName;
    }


    public void setCompanyName (String companyName) {
        this.companyName = companyName;
    }


    public double getPrice() {
        return price;
    }


    public void setPrice(double price) {
        this.price = price;
    }


    public double getPriceChange() {
        return priceChange;
    }


    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }


    public double getChangePercent() {
        return changePercent;
    }


    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }


    @Override
    public String toString() {
        return "Stock {" + "Symbol: " + symbol + ", Company Name: " + companyName + ", " +
                ", Price: " + price + ", Price Change: " + priceChange + ", Change Percent: " + changePercent + "}";
    }


    @Override
    public int compareTo(@NonNull Stock stock) {
        return this.symbol.compareTo(stock.symbol);
    }
}
