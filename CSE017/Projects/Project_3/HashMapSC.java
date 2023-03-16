import java.util.ArrayList;
import java.util.LinkedList;
/**
 * Class HashMap an implementation of the hash table
 * data structure using separate chaining
 */
public class HashMapSC <K, V> {
	private int size;
	private double loadFactor;
	private LinkedList<MapEntry<K,V>>[] hashTable;
	public static int collisions; 
	public static int iterations;

   /**
   * Default constructor
   * Creates an empty hash table with capacity 100
   * and default load factor of 0.9
   */
	public HashMapSC() {
		this(100, 0.9);
	}
   /**
   * Constructor with one parameters
   * Creates an empty hash table with capacity c
   * and default load factor of 0.9
   */
	public HashMapSC(int c) {
		this(c, 0.9);
	}
	/**
  	* Constructor with two parameters
   	* Creates an empty hash table with capacity c
   	* and load factor lf
  	 */
	public HashMapSC(int c, double lf) {
		hashTable = new LinkedList[trimToPowerOf2(c)];
		loadFactor = lf;
		size = 0;
		collisions = 0;
		iterations = 0;
	}
	/**
	 * Private method to find the closest power of 2
	 * to the capacity of the hash table
	 * @param c desired capacity for the hash table
	 * @return  closest power of 2 to c
	 */
	private int trimToPowerOf2(int c) {
		int capacity = 1;
		while (capacity < c)
			capacity  = capacity << 1;
		return capacity;
	}
	/**
	 * hash method
	 * @param hashCode
	 * @return valid index in the hash table
	 */
	private int hash(int hashCode) {
		return hashCode & (hashTable.length-1);
	}
	/**
	 * Rehash method called when the size of the hashtable
	 * reached lf * capacity
	 */
	private void rehash() {
		ArrayList<MapEntry<K,V>> list = toList();
		hashTable = new LinkedList[hashTable.length << 1];
		size = 0;
		for(MapEntry<K,V> entry: list)
			put(entry.getKey(), entry.getValue());

	}
	/**
	 * Method size
	 * @return the number of elements added to the hash table
	 */
	public int size() {
		return size;
	}
	/**
	 * Method clear
	 * resets all the hash table elements to null
	 * and clears all the linked lists attached to the hash table
	 */
	public void clear() {
		size = 0;
		for(int i=0; i<hashTable.length; i++)
			if(hashTable[i] != null)
				hashTable[i].clear();
	}
	/**
	 * Method isEmpty
	 * @return true if there are no valid data in the hash table
	 */
	public boolean isEmpty() {
		return (size == 0);
	}
	/**
	 * Search method
	 * @param key being searched for
	 * @return true if key is found, false otherwise
	 */
	public boolean containsKey(K key) {
		return get(key) != null;
	}
	/**
	 * Get method
	 * @param key being searched for
	 * @return the value of the hash table entry if the key is found,
	 * null if the key is not found
	 */
	public V get(K key) {
		iterations = 0;
		int HTIndex = hash(key.hashCode());
		if(hashTable[HTIndex] != null) {
			LinkedList<MapEntry<K,V>> ll = hashTable[HTIndex];
			for(MapEntry<K,V> entry: ll) {
				iterations++;
				if(entry.getKey().equals(key))
					return entry.getValue();
			}
		}
		return null;
	}
	/**
	 * Add a new entry to the hash table
	 * @param key key of the entry to be added
	 * @param value value of the entry to be added
	 * @return the old value of the entry if an entry with the same key is found
	 * the parameter value is returned if a new entry has been added
	 */
	public V put(K key, V value) {
		if(get(key) != null) { // The key is in the hash map
			int HTIndex = hash(key.hashCode());
			LinkedList<MapEntry<K,V>> ll;
			ll = hashTable[HTIndex];
			for(MapEntry<K,V> entry: ll) {
				if(entry.getKey().equals(key)) {
					V old = entry.getValue();
					entry.setValue(value);
					return old;
				}
			}
		}
		// check load factor
		if(size >= hashTable.length * loadFactor)
			rehash();
		int HTIndex = hash(key.hashCode());
		//create a new LL if empty
		if(hashTable[HTIndex] == null){
			hashTable[HTIndex] = new LinkedList<>();
		} else {
			collisions++;
		}
		hashTable[HTIndex].add(new MapEntry<>(key, value));
		size++;
		return value;
	}

	/**
	 * Method toList used by rehash
	 * @return all the entries in the hash table in an array list
	 */
	public ArrayList<MapEntry<K,V>> toList(){
		ArrayList<MapEntry<K,V>> list = new ArrayList<>();
		for(int i=0; i< hashTable.length; i++) {
			if(hashTable[i]!= null) {
				LinkedList<MapEntry<K,V>> ll = hashTable[i];
				for(MapEntry<K,V> entry: ll)
					list.add(entry);
			}
		} return list;
	}
	/**
	 * toString method
	 * @return the hashtable entries formatted as a string
	 */
	public String toString() {
		String out = "[";
		for(int i=0; i<hashTable.length; i++) {
			if(hashTable[i]!=null) {
				for(MapEntry<K,V> entry: hashTable[i])
					out += entry.toString();
				out += "\n";
			}
		}
		out += "]";
		return out;
	}

	public int getCollisions() {
		return collisions;
	}
	public int getIterations() {
		return iterations;
	}

}