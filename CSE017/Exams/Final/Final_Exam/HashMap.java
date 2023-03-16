/**
 * Class to implement a hash table using separate chaining
 */
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
public class HashMap <K, V> {
	private int size;
	private double loadFactor;
	private LinkedList<MapEntry<K,V>>[] hashTable;
	
    // Constructors
	public HashMap() {
		this(100, 0.9);
	}
	public HashMap(int c) {
		this(c, 0.9);
	}
	public HashMap(int c, double lf) {
		hashTable = new LinkedList[trimToPowerOf2(c)];
		loadFactor = lf;
		size = 0;
	}
	// private methods
	private int trimToPowerOf2(int c) {
		int capacity = 1;
		while (capacity < c)
			capacity  = capacity << 1;
		return capacity;
	}
	private int hash(int hashCode) {
		return hashCode & (hashTable.length-1);
	}
	private void rehash() {
		ArrayList<MapEntry<K,V>> list = toList();
		hashTable = new LinkedList[hashTable.length << 1];
		size = 0;
		for(MapEntry<K,V> entry: list) 
			put(entry.getKey(), entry.getValue());

	}
	
	// public interface
	public int size() { 
		return size;
	}
	public void clear() { 
		size = 0;
		for(int i=0; i<hashTable.length; i++)
			if(hashTable[i] != null)
				hashTable[i].clear();
	}
	public boolean isEmpty() { 
		return (size == 0);
	}
	// search for key - returns true if found
	public boolean containsKey(K key) {
		if(get(key) != null)
			return true;
		return false;
	}
	// returns the value of key if found, null otherwise
	public V get(K key) {
		int HTIndex = hash(key.hashCode());
		if(hashTable[HTIndex] != null) {
			LinkedList<MapEntry<K,V>> ll = hashTable[HTIndex];
			Iterator<MapEntry<K,V>> iter = ll.iterator();
			while(iter.hasNext()) {
				MapEntry<K,V> entry = iter.next();
				if(entry.getKey().equals(key))
					return entry.getValue();
			}
		}
		return null;
	}
	
	// adds a new key or modifies an existing key
	public V put(K key, V value) {
		if(get(key) != null) { // The key is in the hash map
			int HTIndex = hash(key.hashCode());
			LinkedList<MapEntry<K,V>> ll;
			ll = hashTable[HTIndex];
			Iterator<MapEntry<K,V>> iter = ll.iterator();
			while(iter.hasNext()) {
				MapEntry<K,V> entry = iter.next();
				if(entry.getKey().equals(key)) {
					V old = entry.getValue();
					entry.setValue(value); 
					return old;
				}
			}
		}
		// key not in the hash map - check load factor first
		if(size >= hashTable.length * loadFactor)
			rehash();
		int HTIndex = hash(key.hashCode());
		//create a new LL if empty
		if(hashTable[HTIndex] == null){
			hashTable[HTIndex] = new LinkedList<>();
		}
		hashTable[HTIndex].add(new MapEntry<>(key, value));
		size++; 
		return value;
	}
	// returns the elements of the hash map as a list
	public ArrayList<MapEntry<K,V>> toList(){
		ArrayList<MapEntry<K,V>> list = new ArrayList<>();
		for(int i=0; i< hashTable.length; i++) {
			if(hashTable[i]!= null) {
				LinkedList<MapEntry<K,V>> ll = hashTable[i];
				Iterator<MapEntry<K,V>> iter = ll.iterator();
				while(iter.hasNext()) {
					MapEntry<K,V> entry = iter.next();
					list.add(entry);
				}
			}
		} 
		return list;
	}
	/**
	 * Method to return the values of the elements in the hash map
	 * @return array list of the values of the elements in the hash table
	 */

     // Time complexity: O(n)
    public ArrayList<V> values(){
		ArrayList<V> list = new ArrayList<>();
        for (int i = 0; i < hashTable.length; i++) { // O(n)
            if (hashTable[i] !=  null) {
                LinkedList<MapEntry<K,V>> ll = hashTable[i];
			    Iterator<MapEntry<K,V>> iter = ll.iterator();
			    while(iter.hasNext()) { // O(1) since relativly small
				    MapEntry<K,V> entry = iter.next();
				    list.add(entry.getValue());
                }
            }
        }
		return list;
    }
	// returns the elements of the hash map as a string
	public String toString() {
		String out = "[";
		for(int i=0; i<hashTable.length; i++) {
			if(hashTable[i]!=null) {
				LinkedList<MapEntry<K,V>> ll = hashTable[i];
				Iterator<MapEntry<K,V>> iter = ll.iterator();
				while(iter.hasNext()) {
					MapEntry<K,V> entry = iter.next();
					out += entry.toString();
				}
				out += "\n";
			}
		}
		out += "]"; return out;
	}
}