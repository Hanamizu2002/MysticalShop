package com.mysticalkingdoms.mysticalshop.utils;

import com.mysticalkingdoms.mysticalshop.shop.items.ShopItem;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCollection {
    private final Map<String, ShopItem> lookupByName = new HashMap<>();
    private final NavigableMap<Double, ShopItem> map = new TreeMap<>();
    private double total = 0;

    public void add(ShopItem result) {
        double weight = result.weight();

        if (weight <= 0) {
            return;
        }

        total += weight;
        map.put(total, result);
        lookupByName.put(result.id(), result);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public ShopItem getItemByName(String name) {
        return lookupByName.get(name);
    }

    public int size() {
        return map.size();
    }

    public ShopItem next() {
        if (isEmpty()) {
            return null;
        }

        double value = ThreadLocalRandom.current().nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}