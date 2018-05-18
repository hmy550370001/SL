package com.idc9000.smartlock.utils.bleutils;

public class Pair<K, V> implements java.io.Serializable {

    private static final long serialVersionUID = -2709381415522731004L;

    private K key;
    
    private V value;

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
