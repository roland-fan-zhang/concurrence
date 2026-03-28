package fr.uge.concurrence.heat;

import java.util.ArrayList;
import java.util.List;

public class Application {

  void main() throws InterruptedException {
    var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");

    var temperatures = new ArrayList<Integer>();
    var transfert = new Transfert();
    for (String room : rooms) {
      Thread.ofPlatform().start(() -> {
        try {
          var temperature = Heat4J.retrieveTemperature(room);
          transfert.put(temperature);
          System.out.println("Temperature in room " + room + " : " + temperature);
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
      });
    }
    for (int i = 0; i < rooms.size(); i++) {
      temperatures.add(transfert.take());
    }
    System.out.println(temperatures.stream().mapToInt(Integer::intValue).average().getAsDouble());
  }
}