package fr.uge.concurrence.exchanger;

public class ExchangerExample {
  void main() throws InterruptedException {
    var exchanger = new Exchanger<String>();
    Thread.ofPlatform().start(() -> {
      try {
        System.out.println("thread 1 " + exchanger.exchange("foo1"));
      }catch(InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    System.out.println("main " + exchanger.exchange(null));
  }
}
