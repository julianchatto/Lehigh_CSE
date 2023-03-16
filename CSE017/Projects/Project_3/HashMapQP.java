import java.util.ArrayList;

/**
 * Class HashMap an implementation of the hash table
 * data structure using separate chaining
 */
public class HashMapQP <K, V> {
	private int size;
	private double loadFactor;
	private MapEntry<K, V>[] hashTable;
	public static int collisions;
	public static int iterations;

   /**
   * Default constructor
   * Creates an empty hash table with capacity 100
   * and default load factor of 0.9
   */
	public HashMapQP() {
		this(100, 0.5); 

	}
   /**
   * Constructor with one parameters
   * Creates an empty hash table with capacity c
   * and default load factor of 0.9
   */
	public HashMapQP(int c) {
		this(c, 0.5);
	}
	/**
  	* Constructor with two parameters
   	* Creates an empty hash table with capacity c
   	* and load factor lf
  	 */
	public HashMapQP(int c, double lf) {
		hashTable = new MapEntry[trimToPowerOf2(c)];
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
		hashTable = new MapEntry[hashTable.length << 1];
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
			hashTable[i] = null;
	}
	/**
	 * Method isEmpty
	 * @return true if there are no valid data in the hash table
	 */
	public boolean isEmpty() {
		return size == 0;
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
		int result = HTIndex(key, false);
		if (result != -1) {
			if (hashTable[result] == null) {
				return null;
			}
			return hashTable[result].getValue();
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
		int HTIndex;
		if(get(key) != null) { // The key is in the hash map
			HTIndex = HTIndex(key, false);
			V old = hashTable[HTIndex].getValue();
			hashTable[HTIndex].setValue(value);
			return old;
		}
		// check load factor
		if(size >= hashTable.length * loadFactor)
			rehash();
		HTIndex = HTIndex(key, true);
		hashTable[HTIndex] = new MapEntry<>(key, value);
		size++;
		return value;
	}

	public int HTIndex(K key, boolean modify) { // add a boolean that is true if the method is called from put
		int HTIndex = hash(key.hashCode());
		if (hashTable[HTIndex] == null) {
			iterations++;
			return HTIndex;
		} else if(modify){
			collisions++; // increment collisions here only if it is called from the second part of put
		}
		if (hashTable[HTIndex].getKey().equals(key)) {
			iterations++;
			return HTIndex;
		}
		int i = 1;
		int temp = HTIndex;
		while (hashTable[HTIndex] != null) {
			iterations++;
			if (hashTable[HTIndex].getKey().equals(key)) {
				return HTIndex;
			}
			HTIndex = (temp + (int) Math.pow(i, 2)) % hashTable.length;
			i++;
		}
		if (hashTable[HTIndex] == null) {
			return HTIndex;
		}
		return -1;
	}
	/**
	 * Method toList used by rehash
	 * @return all the entries in the hash table in an array list
	 */
	public ArrayList<MapEntry<K,V>> toList(){
		ArrayList<MapEntry<K,V>> list = new ArrayList<>();
		for(int i=0; i< hashTable.length; i++) {
			if(hashTable[i] != null) {
				list.add(hashTable[i]);
			}
		}
		return list;
	}
	/**
	 * toString method
	 * @return the hashtable entries formatted as a string
	 */
	public String toString() {
		String out = "[";
		for(int i=0; i<hashTable.length; i++) {
			out += hashTable[i] + "\n"; // hashTable[i].toString() ???
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
