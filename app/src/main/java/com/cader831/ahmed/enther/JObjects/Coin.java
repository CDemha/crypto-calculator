package com.cader831.ahmed.enther.JObjects;

import java.io.Serializable;

/**
 * Created by Ahmed on 8/01/2018.
 */

public class Coin implements Comparable, Serializable {

    private int Id;

    private String Symbol;

    private String CoinName;

    private boolean isFavourite;

    private boolean isLocalCurrency;

    public Coin(int id, String symbol, String coinName, boolean isFavourite, boolean isLocalCurrency) {
        Id = id;
        Symbol = symbol;
        CoinName = coinName;
        this.isFavourite = isFavourite;
        this.isLocalCurrency = isLocalCurrency;
    }

    public int getId() {
        return Id;
    }

    public boolean isLocalCurrency() {
        return isLocalCurrency;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public String getShortName() {
        return Symbol;
    }

    public String getLongName() {
        return CoinName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Coin) {
            Coin coin = (Coin) o;
            if (CoinName.equals(coin.CoinName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        Coin other = (Coin) o;
        return CoinName.compareToIgnoreCase(other.CoinName);
    }

    @Override
    public String toString() {
        return this.CoinName;
    }
}
