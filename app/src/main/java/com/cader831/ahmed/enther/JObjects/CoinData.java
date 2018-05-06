package com.cader831.ahmed.enther.JObjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CoinData implements Serializable, Comparable {

    private static final long serialVersionUID = 2L;

    private Coin primaryCoin;
    private Coin secondaryCoin;
    private Exchange exchange;
    private BigDecimal downloadPrice;
    private BigDecimal calculatedAmount;
    private BigDecimal givenUnit;
    private Date lastUpdate;

    public CoinData(Coin primaryCoin, Coin secondaryCoin, Exchange exchange, BigDecimal downloadPrice, BigDecimal givenUnit, Date lastUpdate) {
        this.primaryCoin = primaryCoin;
        this.secondaryCoin = secondaryCoin;
        this.exchange = exchange;
        this.downloadPrice = downloadPrice;
        this.givenUnit = givenUnit;
        this.lastUpdate = lastUpdate;
        calculateAmount(givenUnit);
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

    public void calculateAmount(BigDecimal givenUnit) {
        if (givenUnit == new BigDecimal(0, MathContext.DECIMAL64)) {
            this.givenUnit = new BigDecimal(1, MathContext.DECIMAL64);
        }
        this.calculatedAmount = downloadPrice.multiply(getGivenUnit());
    }

    public void setGivenUnit(BigDecimal givenUnit) {
        this.givenUnit = givenUnit;
        calculateAmount(givenUnit);
    }

    public BigDecimal getCalculatedAmount() {
        return calculatedAmount;
    }

    public BigDecimal getGivenUnit() {
        return givenUnit;
    }

    @Override
    public String toString() {
        SimpleDateFormat lastUpdateDate = new SimpleDateFormat("d/M/YY h:mm", Locale.US);
        if (getExchange().getName().equals("Global Average")) {
            return String.format("%s-%s: Price: %.8f at %s", getPrimaryCoin().getShortName(), getSecondaryCoin().getShortName(), getDownloadPrice(), lastUpdateDate.format(lastUpdate));
        }
        return String.format("%s-%s: %s, Price: %.8f at %s", getPrimaryCoin().getShortName(), getSecondaryCoin().getShortName(), getExchange(), getDownloadPrice(), lastUpdateDate.format(lastUpdate));
    }

    @Override
    public int compareTo(Object o) {
        CoinData other = (CoinData) o;
        return other.lastUpdate.compareTo(lastUpdate);
    }
}
