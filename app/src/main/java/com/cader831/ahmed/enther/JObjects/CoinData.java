package com.cader831.ahmed.enther.JObjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CoinData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Coin primaryCoin;
    private Coin secondaryCoin;
    private Exchange exchange;
    private BigDecimal downloadPrice;
    private BigDecimal definedAmount;
    private Date lastUpdate;

    public CoinData(Coin primaryCoin, Coin secondaryCoin, Exchange exchange, BigDecimal downloadPrice, Date lastUpdate) {
        this.primaryCoin = primaryCoin;
        this.secondaryCoin = secondaryCoin;
        this.exchange = exchange;
        this.downloadPrice = downloadPrice;
        this.lastUpdate = lastUpdate;
        this.definedAmount = new BigDecimal(0, MathContext.DECIMAL64);
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

    public void setDefinedAmount(BigDecimal definedAmount) {
        this.definedAmount = definedAmount;
    }

    public BigDecimal getDefinedAmount() {
        return definedAmount;
    }

    @Override
    public String toString() {
        SimpleDateFormat lastUpdateDate = new SimpleDateFormat("EEE, d MMM yyyy h:mm:ss", Locale.US);
        if (getExchange().getName().equals("Global Average")) {
            return String.format("%s-%s: Price: %.8f, Defined Price: %.8f at %s", getPrimaryCoin().getShortName(), getSecondaryCoin().getShortName(), getDownloadPrice(), getDefinedAmount() ,lastUpdateDate.format(lastUpdate));
        }
        return String.format("%s-%s-%s: Price: %.8f, Defined Price: %.8f at %s", getPrimaryCoin().getShortName(), getSecondaryCoin().getShortName(), getExchange(), getDownloadPrice(), getDefinedAmount() ,lastUpdateDate.format(lastUpdate));
    }
}
