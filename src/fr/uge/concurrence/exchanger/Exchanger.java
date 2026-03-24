package fr.uge.concurrence.exchanger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class Exchanger<T> {
//    private final Object lock = new Object();

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private T depositedValue;
    private boolean isPresent;

    public T exchange(T value) throws InterruptedException {
        lock.lock();
        try{
            if (!isPresent) {
                depositedValue = value;
                isPresent = true;
                while (isPresent) {
                    condition.await();
                }
                return depositedValue;
            }
            var firstValue = depositedValue;
            depositedValue = value;
            isPresent = false;
            condition.signal();
            return firstValue;
        }finally {
            lock.unlock();
        }
    }
//    public T exchange(T value) throws InterruptedException {
//        synchronized (lock) {
//            if (!isPresent) {
//                depositedValue = value;
//                isPresent = true;
//                while (isPresent) {
//                    lock.wait();
//                }
//                return depositedValue;
//            }
//            var firstValue = depositedValue;
//            depositedValue = value;
//            isPresent = false;
//            lock.notify();
//            return firstValue;
//        }
//    }
}
