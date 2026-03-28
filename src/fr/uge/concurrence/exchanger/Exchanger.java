package fr.uge.concurrence.exchanger;

public final class Exchanger<T> {

  private final Object lock = new Object();

  private T depositedValue;
  private boolean isPresent;

  public T exchange(T value) throws InterruptedException {
    synchronized (lock) {
      if (!isPresent) {
        depositedValue = value;
        isPresent = true;
        while (isPresent) {
          lock.wait();
        }
        return depositedValue;
      }
      var firstValue = depositedValue;
      depositedValue = value;
      isPresent = false;
      lock.notifyAll();
      return firstValue;
    }
  }
}
