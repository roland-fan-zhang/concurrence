package fr.uge.concurrence.heat;

public class Transfert {
  private final Object lock = new Object();

  private int value;
  private boolean hasValue;

  public void put(int value) throws InterruptedException {
    synchronized (lock){
      while (hasValue){
        lock.wait();
      }
      this.value = value;
      hasValue = true;
      lock.notifyAll();
    }
  }

  public int take() throws InterruptedException {
    synchronized (lock){
      while (!hasValue){
        lock.wait();
      }
      var result = value;
      hasValue = false;
      lock.notifyAll();
      return result;
    }
  }
}
