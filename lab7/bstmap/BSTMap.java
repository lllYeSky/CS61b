package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

    private static class BSTNode<K, V>{
        private K key;
        private V value;
        private BSTNode<K, V> left, right;

        public BSTNode(K key, V value){
            this.key = key;
            this.value = value;
        }

    }
    private BSTNode<K, V> root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public void clear(){
        root = null;
        size = 0;
    }

    @Override
    public void put(K key, V value){
        root = puthelper(root, key, value);
    }

    private BSTNode<K, V> puthelper(BSTNode<K, V> node, K key, V value){
        if(node == null){
            size++;
            return new BSTNode<K, V>(key, value);
        }
        int cmp = key.compareTo(node.key);
        if(cmp > 0){
            node.right = puthelper(node.right, key, value);
        }
        else if(cmp < 0){
            node.left = puthelper(node.left, key, value);
        }
        else{
            node.value = value;
        }
        return node;
    }

    @Override
    public V get(K key){
        if (key == null) {
            return null;
        }
        return gethelper(root, key);
    }

    private V gethelper(BSTNode<K, V> node, K key) {
        if(node == null){
            return null;
        }
        int cmp = key.compareTo(node.key);
        if(cmp > 0){
            return gethelper(node.right, key);
        }
        else if(cmp < 0){
            return gethelper(node.left, key);
        }
        return node.value;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        return containsKeyHelper(root, key);
    }

    private boolean containsKeyHelper(BSTNode<K, V> node, K key) {
        if (node == null) {
            return false;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return containsKeyHelper(node.left, key);
        } else if (cmp > 0) {
            return containsKeyHelper(node.right, key);
        } else {
            return true;
        }
    }

    public void printInOrder() {
        printInOrderHelper(root);
        System.out.println();
    }

    private void printInOrderHelper(BSTNode<K, V> node) {
        if (node == null) return;
        printInOrderHelper(node.left);
        System.out.println(node.key + ": " + node.value);
        printInOrderHelper(node.right);
    }


    @Override
    public V remove(K key) {
        V removedValue = get(key);
        if (removedValue != null) {
            root = removeHelper(root, key);
            size--;
        }
        return removedValue;
    }

    private BSTNode<K, V> removeHelper(BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = removeHelper(node.left, key);
        } else if (cmp > 0) {
            node.right = removeHelper(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            BSTNode<K, V> minNode = findMin(node.right);
            node.key = minNode.key;
            node.value = minNode.value;
            node.right = removeMin(node.right);
        }
        return node;
    }

    private BSTNode<K, V> findMin(BSTNode<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private BSTNode<K, V> removeMin(BSTNode<K, V> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = removeMin(node.left);
        return node;
    }

    @Override
    public Set<K> keySet() {
        Set<K> all = new HashSet<>();
        return keySethelper(all, root);
    }

    private Set<K> keySethelper(Set<K> all, BSTNode<K, V> node){
        if(node == null){
            return all;
        }
        all = keySethelper(all, node.left);
        all.add(node.key);
        all = keySethelper(all, node.right);
        return all;
    }

    @Override
    public V remove(K key, V value){
        if (containsKey(key)){
            BSTNode<K, V> tar = findkey(root, key);
            if(tar.value.equals(value)){
                remove(key);
                return value;
            }
        }
        return null;
    }

    private BSTNode<K, V> findkey(BSTNode<K, V> node, K key){
        if (node == null){
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node = findkey(node.left, key);
        } else if (cmp > 0) {
            node = findkey(node.right, key);
        }
        return node;
    }

    private class BSTIterator implements Iterator<K> {
        private Stack<BSTNode<K, V>> stack;

        public BSTIterator() {
            stack = new Stack<>();
            pushLeft(root);
        }

        private void pushLeft(BSTNode<K, V> node) {
            if(node == null){
                return;
            }
            stack.add(node);
            pushLeft(node.left);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public K next() {
            if (stack.isEmpty()){
                throw new NoSuchElementException();
            }
            BSTNode<K, V> top = stack.pop();
            pushLeft(top.right);
            return top.key;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator();
    }
}
