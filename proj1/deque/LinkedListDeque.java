package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T>{
    private Intnode senf;
    private Intnode senb;
    private int size;

    public class Intnode{
        public Intnode prev;
        public Intnode next;
        public T item;
    }

    private class LLDiterator implements Iterator<T>{
        private Intnode current;

        public LLDiterator(){
            current = senf.next;
        }

        public boolean hasNext(){
            if(current == senb){
                return false;
            }
            return true;
        }

        public T next(){
            Intnode t = current;
            current = t.next;
            return t.item;
        }
    }

    public LinkedListDeque(){
        size = 0;
        senf = new Intnode();
        senb = new Intnode();
        senf.prev = null; senb.next = null;
        senf.next = senb; senb.prev = senf;
    }

    @Override
    public void addFirst(T item){
        Intnode n = new Intnode();
        n.item = item;
        n.prev = senf; n.next = senf.next;
        senf.next.prev = n; senf.next = n;
        size += 1;
    }

    @Override
    public void addLast(T item){
        Intnode n = new Intnode();
        n.item = item;
        n.next = senb; n.prev = senb.prev;
        senb.prev.next = n; senb.prev = n;
        size += 1;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque(){
        Intnode in = senf.next;
        while(in != senb){
            System.out.print(in.item + " ");
            in = in.next;
        }
        System.out.print("\n");
    }

    @Override
    public T removeFirst(){
        if(size == 0){
            return null;
        }
        size--;
        Intnode ans = senf.next;
        senf.next = ans.next;
        ans.next.prev = senf;
        return ans.item;
    }

    @Override
    public T removeLast(){
        if(size == 0){
            return null;
        }
        size--;
        Intnode ans = senb.prev;
        senb.prev = ans.prev;
        ans.prev.next = senb;
        return ans.item;
    }

    @Override
    public T get(int index) {
        if(size < index + 1){
            return null;
        }
        Intnode in = senf.next;
        for(int i = 0; i<index; i++){
            in = in.next;
        }
        return in.item;
    }

    public Iterator<T> iterator(){
        return new LLDiterator();
    }

    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if(other.size() != this.size()){
            return false;
        }

        Iterator<T> it1 = this.iterator();
        Iterator<T> it2 = ((Iterable<T>) other).iterator();

        while (it1.hasNext() && it2.hasNext()) {
            T a = it1.next();
            T b = it2.next();
            if (a == null && b == null) continue;
            if (a == null || b == null) return false;
            if (!a.equals(b)) return false;
        }
        return true;
    }

    private T getRecursiveHelper(Intnode node, int index){
        if(node == null || node == senb) {
            return null;
        }
        if(index == 0){
            return node.item;
        }
        return getRecursiveHelper(node.next, index-1);
    }

    public T getRecursive(int index){
        return getRecursiveHelper(senf.next, index);
    }
}
