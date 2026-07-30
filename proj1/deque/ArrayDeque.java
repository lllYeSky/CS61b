package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T>{
    public T[] ar;
    private int size, fir, las;

    public ArrayDeque(){
        ar = (T[]) new Object[8];
        size = 0;
        fir = las = 3;
    }

    private class ADiterator implements Iterator<T> {
        private int cur;

        public ADiterator(){
            cur = 0;
        }

        public boolean hasNext(){
            if(cur < size){
                return true;
            }
            return false;
        }

        public T next(){
            if(cur >= size){
                return null;
            }
            int in = (cur + fir) % ar.length;
            cur++;
            return ar[in];
        }
    }

    public void resize(int capacity){
        T[] a = (T[]) new Object[capacity];
        for(int i = 0; i<size; i++){
            int in = (fir + i) % ar.length;
            a[i] = ar[in];
        }
        ar = a;
        fir = 0;
        las = size;
    }

    @Override
    public void addFirst(T item){
        if(size == ar.length){
            resize((int)(size * 1.2));
        }
        fir = (fir - 1 + ar.length) % ar.length;
        ar[fir] = item;
        size++;
    }

    @Override
    public void addLast(T item){
        if(size == ar.length){
            resize((int)(size * 1.2));
        }
        ar[las] = item;
        las = (las + 1 + ar.length) % ar.length;
        size++;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque(){
        int i = fir;
        while(i != las){
            System.out.print(ar[i] + " ");
            i = (i + 1 + ar.length) % ar.length;
        }
        System.out.print("\n");
    }

    @Override
    public T removeFirst(){
        if(size == 0){
            return null;
        }
        T t = ar[fir];
        ar[fir] = null;
        fir = (fir + 1) % ar.length;
        size--;
        if(size < ar.length / 4){
            resize(ar.length / 2);
        }
        return t;
    }

    @Override
    public T removeLast(){
        if(size == 0){
            return null;
        }
        las = (las - 1 + ar.length) % ar.length;
        T t = ar[las];
        ar[las] = null;
        size--;
        if(size < ar.length / 4){
            resize(ar.length / 2);
        }
        return t;
    }

    @Override
    public T get(int index){
        int re = (index + fir) % ar.length;
        return ar[re];
    }

    public Iterator<T> iterator(){
        return new ADiterator();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deque)) return false;

        Deque<T> other = (Deque<T>) o;
        if (this.size() != other.size()) return false;

        Iterator<T> it1 = this.iterator();
        Iterator<T> it2 = ((Iterable<T>) other).iterator();

        while (it1.hasNext() && it2.hasNext()) {
            if (!java.util.Objects.equals(it1.next(), it2.next())) {
                return false;
            }
        }
        return true;
    }


}
