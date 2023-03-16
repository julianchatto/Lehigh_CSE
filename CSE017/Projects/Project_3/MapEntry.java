public class MapEntry<K, V> {
    private K key;
    private V value;
    public MapEntry(K k, V v) {
        key = k;
        value = v;
    }
    public K getKey() { 
        return key; 
    }
    public V getValue() { 
        return value; 
    }
    public void setKey(K k) {
        key = k;
    }
    public void setValue(V v) {
        value=v;
    }
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}