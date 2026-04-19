package fr.uge.concurrence.exchanger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicExchangerReentrantLock<T> {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

  private final T[] values;
  private final int nbParticipants;
  private int counter;
  private boolean ready;

  @SuppressWarnings("unchecked")
  public CyclicExchangerReentrantLock(int nbParticipants){
    this.nbParticipants = nbParticipants;
    this.values = (T[]) new Object[nbParticipants];
  }

  public T exchange(T value) throws InterruptedException {
    lock.lock();
    try {
      if (ready) {
        throw new IllegalStateException();
      }
      var index = counter;
      values[counter] = value;
      counter++;
      if(counter == nbParticipants){
        ready = true;
        condition.signalAll();
      }
      while(!ready){
        condition.await();
      }
      return values[(index + 1) % nbParticipants];
    }finally {
      lock.unlock();
    }
  }

  static void main() {
    var exchanger = new CyclicExchangerReentrantLock<Integer>(5);

    for (int i = 0; i < 5; i++) {
      final int threadIndex = i;
      Thread.ofPlatform().start(() -> {
        try {
          Thread.sleep(threadIndex * 1_000L);
          var result = exchanger.exchange(threadIndex);
          System.out.println("Thread " + threadIndex + " a reçu : " + result);
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
      });
    }
  }
}
