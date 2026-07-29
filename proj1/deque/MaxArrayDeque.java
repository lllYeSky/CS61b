package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> com;

    public MaxArrayDeque(Comparator<T> c){
        super();
        this.com = c;
    }

    public T max(){
        if(size() == 0){
            return null;
        }
        T ma = get(0);
        for(int i = 1; i < size(); i++){
            T cur = get(i);
            if(com.compare(ma, cur) > 0){
                ma = cur;
            }
        }
        return ma;
    }

    public T max(Comparator<T> c){
        if(size() == 0){
            return null;
        }
        T ma = get(0);
        for(int i = 1; i < size(); i++){
            T cur = get(i);
            if(c.compare(ma, cur) > 0){
                ma = cur;
            }
        }
        return ma;
    }
}
