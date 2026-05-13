package it.unibo.assignment01.util;

import java.util.Optional;

public interface BoundedBuffer<Item> {

    public void put(Item item) throws InterruptedException;
    
    public Item get() throws InterruptedException;
    
    public Optional<Item> lazyGet();
}
