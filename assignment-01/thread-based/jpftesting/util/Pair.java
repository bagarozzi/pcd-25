package jpftesting.util;

public class Pair <T,K> {
    private final T first;
    private final K second;

    public Pair(T first, K second) {
        this.first = first;
        this.second = second;
    }

    public T getX() {
        return this.first;
    }
    

    public K getY() {
        return this.second;
    }
}