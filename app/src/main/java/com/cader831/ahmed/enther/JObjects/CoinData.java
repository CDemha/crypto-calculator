package com.cader831.ahmed.enther.JObjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoinData implements Serializable {
    private Coin primaryCoin;
    private Coin secondaryCoin;
    private BigDecimal downloadPrice;
    private BigDecimal calculatedPrice;
    private Date lastUpdate;
    private BigDecimal priceHigh;
    private BigDecimal priceLow;
    private Exchange exchange;


    public CoinData(Coin primaryCoin, Coin secondaryCoin, Exchange exchange, BigDecimal downloadPrice ,BigDecimal priceHigh, BigDecimal priceLow, Date lastUpdate) {
        this.primaryCoin = primaryCoin;
        this.secondaryCoin = secondaryCoin;
        this.exchange = exchange;
        this.downloadPrice = downloadPrice;
        this.lastUpdate = lastUpdate;
        this.priceHigh = priceHigh;
        this.priceLow = priceLow;
    }

    public BigDecimal getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(BigDecimal calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
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

    public BigDecimal getDownloadPrice() {
        return downloadPrice;
    }

    @Override
    public String toString() {
        SimpleDateFormat lastUpdateDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        return String.format("(%s-%s-%s): [DPrice: %.8f, CPrice: %.8f] at %s", primaryCoin.getShortName(), secondaryCoin.getShortName(), exchange, downloadPrice, calculatedPrice, lastUpdateDate);
    }
}
