package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author lllYeSky
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private Collection<Node>[] buckets;
    private int allnode;
    private double loadFactor;

    public MyHashMap() {
        allnode = 0;
        loadFactor = 0.75;
        buckets = createTable(16);
    }

    public MyHashMap(int initialSize) {
        allnode = 0;
        loadFactor = 0.75;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        allnode = 0;
        loadFactor = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] newTable = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            newTable[i] = createBucket();
        }
        return newTable;
    }

    @Override
    public void clear(){
        buckets = createTable(16);
        allnode = 0;
    }

    @Override
    public boolean containsKey(K key){
        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);
        int index = Math.floorMod(hash, buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket == null || bucket.isEmpty()) {
            return false;
        }
        for(Node n : bucket){
            if(n.key.equals(key)){
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key){
        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);
        int index = Math.floorMod(hash, buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        for(Node n : bucket){
            if(n.key.equals(key)){
                return n.value;
            }
        }
        return null;
    }

    @Override
    public int size(){
        return allnode;
    }

    @Override
    public void put(K key, V value){
        if ((double) allnode / buckets.length > loadFactor) {
            resize(buckets.length * 2);
        }
        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);
        int index = Math.floorMod(hash, buckets.length);
        Collection<Node> bucket = buckets[index];
        for(Node n : bucket){
            if(n.key.equals(key)){
                n.value = value;
                return;
            }
        }
        allnode++;
        Node node = new Node(key, value);
        bucket.add(node);
    }

    private void resize(int newSize) {
        Collection<Node>[] oldBuckets = buckets;
        buckets = createTable(newSize);
        allnode = 0;
        for (Collection<Node> bucket : oldBuckets) {
            if (bucket != null && !bucket.isEmpty()) {
                for (Node n : bucket) {
                    int hash = n.key.hashCode();
                    hash = hash ^ (hash >>> 16);
                    int newIndex = Math.floorMod(hash, buckets.length);
                    buckets[newIndex].add(n);
                    allnode++;
                }
            }
        }
    }

    @Override
    public Set<K> keySet(){
        Set<K> set = new HashSet<>();
        for(int i = 0; i < buckets.length; i++){
            for (Node n : buckets[i]){
                set.add(n.key);
            }
        }
        return set;
    }

    @Override
    public Iterator<K> iterator() {
        ArrayList<K> array = new ArrayList<>();
        for(int i = 0; i < buckets.length; i++){
            for (Node n : buckets[i]){
                array.add(n.key);
            }
        }
        return array.iterator();
    }

    @Override
    public V remove(K key){
        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);
        int index = Math.floorMod(hash, buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        Iterator<Node> iter = bucket.iterator();
        while (iter.hasNext()){
            Node node = iter.next();
            if(node.key.equals(key)){
                iter.remove();
                allnode--;
                return node.value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int hash = key.hashCode();
        hash = hash ^ (hash >>> 16);
        int index = Math.floorMod(hash, buckets.length);
        Collection<Node> bucket = buckets[index];
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        Iterator<Node> iter = bucket.iterator();
        while (iter.hasNext()){
            Node node = iter.next();
            if(node.key.equals(key) && node.value.equals(value)){
                iter.remove();
                allnode--;
                return node.value;
            }
        }
        return null;
    }
}
