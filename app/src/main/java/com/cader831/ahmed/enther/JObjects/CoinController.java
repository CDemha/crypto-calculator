package com.cader831.ahmed.enther.JObjects;

import com.cader831.ahmed.enther.Serializer;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Coin> mapOfCoins = new HashMap<>();
    private Map<String, String> coinNames = new HashMap<>();
    private HashMap<String, ArrayList<CoinData>> coinDataMap = new HashMap<>();

    public CoinController() {

    }

    public Map<String, Coin> getMapOfCoins() {
        return mapOfCoins;
    }

    public void addCoin(Coin coin) {
        coinNames.put(coin.getLongName(), coin.getShortName());
        coinNames.put(coin.getShortName(), coin.getLongName());
        mapOfCoins.put(coin.getLongName(), coin);
    }

    public List<Coin> getCryptosToList() {
        List<Coin> cryptoLists = new ArrayList<>();
        for (Coin coin : getMapOfCoins().values()) {
            if (!coin.isLocalCurrency()) {
                cryptoLists.add(coin);
            }
        }
        return cryptoLists;
    }

    public List<Coin> getLocalsToList() {
        List<Coin> localsList = new ArrayList<>();
        for (Coin coin : getMapOfCoins().values()) {
            if (coin.isLocalCurrency()) {
                localsList.add(coin);
            }
        }
        return localsList;
    }

    public List<Coin> getFavouritesToList() {
        List<Coin> favouritesList = new ArrayList<>();
        for (Coin coin : getMapOfCoins().values()) {
            if (coin.isFavourite()) {
                favouritesList.add(coin);
            }
        }
        return favouritesList;
    }

    public Coin getCoinFromShortName(String shortName) {
        // returns the long name equivalent coin based on the short given name.
        return mapOfCoins.get(coinNames.get(shortName));
    }

    public Coin getCoin(String longName) {
        // returns the coin based on the given long name.
        return mapOfCoins.get(longName);
    }

    public String generateCoinExchangePair(Coin primaryCoin, Coin secondaryCoin, Exchange exchange) {
        return String.format("%s-%s-%s", primaryCoin.getShortName(), secondaryCoin.getShortName(), exchange.getName());
    }

    public String generateCoinPair(Coin primaryCoin, Coin secondaryCoin) {
        return String.format("%s-%s", primaryCoin.getShortName(), secondaryCoin.getShortName());
    }

    public void addToHistory(CoinData coinData) {
        String coinPair = generateCoinPair(coinData.getPrimaryCoin(), coinData.getSecondaryCoin());
        if (coinDataMap.containsKey(coinPair)) {
            if (!coinDataExist(coinData, coinPair)) {
                coinDataMap.get(coinPair).add(coinData);
            }

        } else {
            ArrayList<CoinData> listOfCoinData = new ArrayList();
            listOfCoinData.add(coinData);
            coinDataMap.put(coinPair, listOfCoinData);
        }
    }

    private boolean coinDataExist(CoinData coinData, String coinPair) {
        SimpleDateFormat compareFormat = new SimpleDateFormat("d/M/YY h:mm");

        String newCoinDataDate = compareFormat.format(coinData.getLastUpdate());
        for (CoinData c : coinDataMap.get(coinPair)) {
            String existingCoinDate = compareFormat.format(c.getLastUpdate());
            if (newCoinDataDate.equals(existingCoinDate) & coinData.getCalculatedAmount().equals(c.getCalculatedAmount()) & coinData.getExchange().equals(c.getExchange()) & coinData.getGivenUnit().equals(c.getGivenUnit())) {
                return true;
            }
        }
        return false;
    }

    public void setCoinData(CoinData coinData, File file) {
        addToHistory(coinData);
        Serializer.Serialize(file, this);
    }

    public HashMap<String, ArrayList<CoinData>> getCoinDataMap() {
        return coinDataMap;
    }

    public CoinData getCoinData(String coinPair) {

        if (coinDataMap.get(coinPair) == null) {
            return null;
        }
        Collections.sort(coinDataMap.get(coinPair));
        return coinDataMap.get(coinPair).get(0);

    }
}
