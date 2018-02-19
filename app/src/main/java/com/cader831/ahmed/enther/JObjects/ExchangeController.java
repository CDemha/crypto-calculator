package com.cader831.ahmed.enther.JObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeController implements Serializable {

    private static final long serialVersionUID = 1L;

    Map<String, Exchange> ExchangeMap = new HashMap<>();

    public ExchangeController() {

    }

    public ExchangeController(Map<String, Exchange> ExchangeMap) {
        this.ExchangeMap = ExchangeMap;
    }

    public Map<String, Exchange> getExchangeMap() {
        return ExchangeMap;
    }

    public void AddExchange(String exchangeName, Exchange exchange) {
        ExchangeMap.put(exchangeName, exchange);
    }

    public Exchange getExchange(String exchangeName) {
        return this.ExchangeMap.get(exchangeName);
    }

    public List<Exchange> getExchangesToList() {
        List<Exchange> exchangs = new ArrayList<>(getExchangeMap().values());
        return exchangs;
    }
}
