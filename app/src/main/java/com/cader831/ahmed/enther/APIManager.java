package com.cader831.ahmed.enther;

import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.Exchange;

public class APIManager {

    private final String MAIN_PATH = "https://min-api.cryptocompare.com/data/";

    public APIManager() {

    }

    public String generatePriceLink(Coin primaryCoin, Coin secondaryCoin, Exchange exchange) {
        String pricePath = MAIN_PATH + String.format("price?fsym=%s&tsyms=%s&e=%s", primaryCoin.getShortName(), secondaryCoin.getShortName(), exchange.getName());
        return pricePath;
    }

    public String generatePriceLink(Coin primaryCoin, Coin secondaryCoin) {
        String pricePath = MAIN_PATH + String.format("price?fsym=%s&tsyms=%s", primaryCoin.getShortName(), secondaryCoin.getShortName());
        return pricePath;
    }
}
