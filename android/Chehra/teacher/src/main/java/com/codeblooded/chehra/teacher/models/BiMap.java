package com.codeblooded.chehra.teacher.models;

import java.util.HashMap;

public class BiMap<K, V> {

    private HashMap<K, V> map = new HashMap<>();
    private HashMap<V, K> inverseMap = new HashMap<>();

    public HashMap<K, V> getMap() {
        return map;
    }

    public HashMap<V, K> getInverseMap() {
        return inverseMap;
    }

    public void put(K k, V v) {
        map.put(k, v);
        inverseMap.put(v, k);
    }

    public V get(K k) {
        return map.get(k);
    }

    public K getKey(V v) {
        return inverseMap.get(v);
    }

}