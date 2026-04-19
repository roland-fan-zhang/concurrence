package fr.uge.concurrence.exam.exo2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public record Order(int id, Dish dish) {

  public static final int DELIVERY_CAPACITY = 5;

  public Order {
    Objects.requireNonNull(dish);
  }

  public enum Station {
    Grill, Fry, HotFood, ColdPrep, Dessert, Pizza
  }

  public record Dish(String name, Station station) {

    public Dish {
      Objects.requireNonNull(name);
      Objects.requireNonNull(station);
    }
  }


  public static Order nextOrder() throws InterruptedException {
    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 200));
    int id = idCounter++;
    var station = Station.values()[ThreadLocalRandom.current().nextInt(0, Station.values().length)];
    var dish = DISHES[ThreadLocalRandom.current().nextInt(0, DISHES.length)];
    var res = new Order(idCounter, dish);
    System.out.println("Received " + res);
    return res;
  }


  public static void deliver(Collection<Order> orders) throws InterruptedException {
    Objects.requireNonNull(orders);
    if (orders.isEmpty()) {
      return;
    }
    Thread.sleep(50L);
    for (var order : orders) {
      Thread.sleep(50L * ThreadLocalRandom.current().nextInt(1, 4));
    }
    System.out.println("Served " + orders.stream().map(Objects::toString)
        .collect(Collectors.joining("; ", "[", "]")));
  }

  public static void prepareDish(Dish dish) throws InterruptedException {
    System.out.println("Station " + dish.station + " is preparing " + dish);
    Objects.requireNonNull(dish);
    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
    System.out.println("Station " + dish.station + " finished " + dish);
  }

  private static int idCounter = 0;
  private static final Dish[] DISHES = new Dish[]{
      new Dish("PaÃ«lla", Station.HotFood),
      new Dish("Bouillabaisse", Station.HotFood),
      new Dish("CaesarSalad", Station.ColdPrep),
      new Dish("Chicken Biryani", Station.HotFood),
      new Dish("Ratatouille", Station.ColdPrep),
      new Dish("Gazpacho", Station.ColdPrep),
      new Dish("Tortilla", Station.HotFood),
      new Dish("Pizza Margherita", Station.Pizza),
      new Dish("Lahmacun", Station.Pizza),
      new Dish("Pide", Station.Pizza),
      new Dish("Farinata", Station.Pizza),
      new Dish("Asado", Station.Grill),
      new Dish("Picanha", Station.Grill),
      new Dish("Fish and Chips", Station.Fry),
      new Dish("Tempura", Station.Fry),
      new Dish("Moa Meli", Station.HotFood),
      new Dish("Waikiki", Station.HotFood)
  };

  static void main() {
    var grillStation = new ArrayBlockingQueue<Order>(10);
    var fryStation = new ArrayBlockingQueue<Order>(10);
    var hotFoodStation = new ArrayBlockingQueue<Order>(10);
    var coldPrepStation = new ArrayBlockingQueue<Order>(10);
    var dessertStation = new ArrayBlockingQueue<Order>(10);
    var pizzaStation = new ArrayBlockingQueue<Order>(10);
    var stations = List.of(grillStation, fryStation, hotFoodStation, coldPrepStation, dessertStation, pizzaStation);

    var orders = new ArrayBlockingQueue<Order>(10);

    Thread.ofPlatform().name("caisse").start(() -> {
      try {
        for (;;) {
          var order = Order.nextOrder();
          switch (order.dish.station) {
            case Grill -> grillStation.put(order);
            case Fry -> fryStation.put(order);
            case HotFood -> hotFoodStation.put(order);
            case ColdPrep -> coldPrepStation.put(order);
            case Dessert -> dessertStation.put(order);
            case Pizza -> pizzaStation.put(order);
          }
        }
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    for (int i = 0; i < stations.size(); i++) {
      int finalI = i;
      Thread.ofPlatform().start(() -> {
        try {
          for(;;) {
            var order = stations.get(finalI).take();
            Order.prepareDish(order.dish);
            orders.put(order);
          }
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
      });
    }
    Thread.ofPlatform().name("livreur").start(() -> {
      try {
        for (;;) {
          var batch = new ArrayList<Order>(Order.DELIVERY_CAPACITY);
          while (batch.size() < Order.DELIVERY_CAPACITY) {
            batch.add(orders.take());
          }
          Order.deliver(batch);
        }
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
  }
}