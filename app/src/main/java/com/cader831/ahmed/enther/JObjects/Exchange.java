package com.cader831.ahmed.enther.JObjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Exchange implements Comparable,Serializable {

    public String Name;
    public boolean isFavourited;
    public HashMap<String, List<Coin>> mapOfCoins = new HashMap<>();

    public Exchange(String Name) {
        this.Name = Name;
        isFavourited = false;
    }

    public String getName() {
        return Name;
    }

    public List<Coin> getSupportedCoins(String coinLongName) {
        return mapOfCoins.get(coinLongName);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Exchange) {
            Exchange exchange = (Exchange) o;
            if (Name.equals(exchange.Name)) {
                return true;
            }
        }
        return false;
    }

    public void addSupportedCoins(Coin coin, List<Coin> arrayOfSupportedCoins) {
        mapOfCoins.put(coin.getLongName(), arrayOfSupportedCoins);
    }

    @Override
    public int compareTo(Object o) {
        Exchange other = (Exchange) o;
        return Name.compareToIgnoreCase(other.Name);
    }

    @Override
    public int hashCode() {
        return Name.hashCode();
    }
}
