package com.cader831.ahmed.enther.JObjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CoinData implements Serializable {
    private Coin primaryCoin;
    private Coin secondaryCoin;
    private BigDecimal price;
    private Date lastUpdate;
    private BigDecimal priceHigh;
    private BigDecimal priceLow;
    private Exchange exchange;


    public CoinData(Coin primaryCoin, Coin secondaryCoin, Exchange exchange, BigDecimal price, BigDecimal priceHigh, BigDecimal priceLow, Date lastUpdate) {
        this.primaryCoin = primaryCoin;
        this.secondaryCoin = secondaryCoin;
        this.exchange = exchange;
        this.price = price;
        this.lastUpdate = lastUpdate;
        this.priceHigh = priceHigh;
        this.priceLow = priceLow;

    }

    public Coin getPrimaryCoin() {
        return primaryCoin;
    }

    public Coin getSecondaryCoin() {
        return secondaryCoin;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("(%s-%s-%s): [Price: %.8f, High: %.8f, Low: %.8f] at %s", primaryCoin.getShortName(), secondaryCoin.getShortName(), exchange, price, priceHigh, priceLow, lastUpdate);
    }
}
