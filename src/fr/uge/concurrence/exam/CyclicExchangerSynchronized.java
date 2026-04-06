package fr.uge.concurrence.exam;

public class CyclicExchangerSynchronized<T> {
  private final Object lock = new Object();

  private final T[] values;
  private final int nbParticipants;
  private int counter;
  private boolean ready;

  @SuppressWarnings("unchecked")
  public CyclicExchangerSynchronized(int nbParticipants){
    this.nbParticipants = nbParticipants;
    this.values = (T[]) new Object[nbParticipants];
  }

  public T exchange(T value) throws InterruptedException {
    synchronized (lock){
      if (ready) {
        throw new IllegalStateException();
      }
      var index = counter;
      values[counter] = value;
      counter++;
      if(counter == nbParticipants){
        ready = true;
        lock.notifyAll();
      }
      while(!ready){
        lock.wait();
      }
      return values[(index + 1) % nbParticipants];
    }
  }

  static void main() {
    var exchanger = new CyclicExchangerSynchronized<Integer>(5);

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
