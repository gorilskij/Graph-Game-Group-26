package game.useful;

import java.util.ArrayList;

// just ArrayList but easier to use
public class GoodList<T> extends ArrayList<T> {
    public T first() {return get(0);} // return first element
    public T last() {return get(size() - 1);} // return last element
    public T pop() {return remove(size() - 1);} // remove and return last element
    // add
    public T shift() {return remove(0);} // remove and return first element
    public void unshift(T value) {add(0, value);} // add element at index 0
    public GoodList<T> shallowClone() {
        var newList = new GoodList<T>();
        for (T value : this) newList.add(value);
        return newList;
    }
}