package fr.uge.concurrence.heat;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TransfertReentrantLock {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

  private int value;
  private boolean hasValue;

  public void put(int value) throws InterruptedException {
    lock.lock();
    try{
      while (hasValue){
        condition.await();
      }
      this.value = value;
      hasValue = true;
      condition.signalAll();
    }finally {
      lock.unlock();
    }
  }

  public int take() throws InterruptedException {
    lock.lock();
    try{
      while (!hasValue){
        condition.await();
      }
      var result = value;
      hasValue = false;
      condition.signalAll();
      return result;
    }finally {
      lock.unlock();
    }
  }
}
