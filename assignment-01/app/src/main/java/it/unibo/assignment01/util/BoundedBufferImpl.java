package it.unibo.assignment01.util;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Parallel implementation of a bounded buffer
 * as a monitor, using reentrant locks
 * To make it parallel
 * @param <Item>
 */
public class BoundedBufferImpl<Item> implements BoundedBuffer<Item> {

	private final LinkedList<Item> buffer;
	private final int maxSize;

	private final Lock lock = new ReentrantLock();

	private final Condition notEmpty = lock.newCondition();
	private final Condition notFull = lock.newCondition();

	public BoundedBufferImpl(int size) {
		this.buffer = new LinkedList<Item>();
		this.maxSize = size;
	}

	@Override
	public void put(Item item) throws InterruptedException {
		lock.lock(); // Ingresso nel Monitor
		try {
			while (isFull()) {
				notFull.await(); // La GUI attende se il buffer è pieno
			}
			buffer.addLast(item);

			// Segnala al Controller che c'è un elemento da consumare
			notEmpty.signalAll();
		} finally {
			lock.unlock(); // Uscita dal Monitor
		}
	}

	@Override
	public Item get() throws InterruptedException {
		lock.lock(); // Ingresso nel Monitor
		try {
			while (isEmpty()) {
				notEmpty.await(); // Il Controller attende se il buffer è vuoto
			}
			Item item = buffer.removeFirst();

			// Segnala alla GUI che si è liberato un posto
			notFull.signalAll();
			return item;
		} finally {
			lock.unlock(); // Uscita dal Monitor
		}
	}

	private boolean isFull() {
		return buffer.size() == maxSize;
	}

	private boolean isEmpty() {
		return buffer.size() == 0;
	}
}