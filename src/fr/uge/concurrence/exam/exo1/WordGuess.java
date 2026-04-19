package fr.uge.concurrence.exam.exo1;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class WordGuess {
  private final String solution;
  private final int nbThreads;

  private static final int CREDITS = 4;

  private final Object lock = new Object();
  private String state;
  private final Map<Thread, Integer> players = new HashMap<>();
  private boolean isReady;
  private boolean isCancelled;
  private boolean canPlay;

  private WordGuess(String solution, int nbThreads) {
    if(nbThreads <= 0){
      throw new IllegalArgumentException("There must be at least one player");
    }
    this.solution = Objects.requireNonNull(solution);
    this.nbThreads = nbThreads;
    super();
    synchronized (lock) {
      state = ".".repeat(solution.length());
    }
  }

  public static WordGuess newWordGuess(int nbThreads) {
    var solution = Words.randomWord();
    return new WordGuess(solution, nbThreads);
  }

  public boolean register() throws InterruptedException {
    synchronized (lock) {
      if(isReady || Thread.interrupted() || isCancelled){
        return false;
      }
      players.put(Thread.currentThread(), CREDITS);
      if(players.size() == nbThreads){
        isReady = true;
        lock.notifyAll();
      }
      while (!isReady) {
        try{
          lock.wait();
        }catch (InterruptedException e) {
          players.remove(Thread.currentThread());
          isCancelled = true;
          lock.notifyAll();
          throw e;
        }
      }
      if (isCancelled) {
        throw new InterruptedException();
      }
      return true;
    }
  }

  public Map<Thread, Integer> credits() {
    synchronized (lock) {
      return Map.copyOf(players);
    }
  }

  private Optional<String> play(char letter) throws InterruptedException {
    synchronized (lock) {
      var response = Words.addLetter(letter, state, solution);
      if(!state.equals(response)){
        state = response;
        return Optional.of(state);
      }
      if(!solution.contains("" + letter)){
        players.computeIfPresent(Thread.currentThread(), (_, credits) -> credits - 1);
      }
      if(players.get(Thread.currentThread()) == 0) {
        canPlay = true;
      }
      if(Words.isComplete(response)) {
        lock.notifyAll();
        return Optional.of(solution);
      }
      if(!players.containsKey(Thread.currentThread())){
        throw new IllegalStateException();
      }
      while(!canPlay){
        lock.wait();
      }
      return Optional.of(state);
    }
  }

  public static void main(String[] args) {
    var nbThreads = 6;
    var game = newWordGuess(nbThreads - 1);
    var threads = new Thread[nbThreads];

    for (int i = 0; i < nbThreads; i++) {
      threads[i] = Thread.ofPlatform().name("thread " + i).start(() -> {
        var name = Thread.currentThread().getName();
        try {
          waitRandom(2000);
          // Inscription
          System.out.println(name + " tries to register");
          if (!game.register()) {
            System.out.println(name + " NOT registered");
            return;
          }
          System.out.println(name + " registered");

					// Jeu
					var already = new StringBuilder();
					while (true) {
						var letter = Words.randomLetter(already.toString());
						already.append(letter);
						System.out.println(name + " plays " + letter);

						var played = game.play(letter);
						if (played.isEmpty()) {
							System.out.println(name + " LOST");
							return;
						}
						var state = played.orElseThrow();
						if (!state.contains(".")) {
							System.out.println(name + " WON: " + state);
							return;
						}
						System.out.println("\t\t " + state);
						already.append(state);

					    waitRandom(1000);
//
//					    // Q4
//						//if (ThreadLocalRandom.current().nextInt(10) == 0) {
//						//	System.out.println("CRASH: " + name);
//						//	Thread.currentThread().interrupt();
//						//}

					}
        } catch (InterruptedException e) {
          for(var thread : game.credits().entrySet()) {
              thread.getKey().interrupt();
          }
        }
      });
    }
  }

  public static void waitRandom(int clue) throws InterruptedException {
    Thread.sleep(clue + Math.abs(ThreadLocalRandom.current().nextInt() % clue));
  }
}