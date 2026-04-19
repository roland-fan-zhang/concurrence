package fr.uge.concurrence.exam.exo1;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class WordGuessCorrection {
  private final String solution;
  private final int nbThreads;

  private static final int CREDITS = 4;

  private final Object lock = new Object();
  private String state;
  private final Map<Thread, Integer> credits = new HashMap<>();
  private boolean isInterrupted;
  private enum Step {REGISTERING, PLAYING, INTERRUPTED, WON, LOST}
  private Step step;
  private int nbLosers;

  private WordGuessCorrection(String solution, int nbThreads) {
    if(nbThreads <= 0){
      throw new IllegalArgumentException("There must be at least one player");
    }
    this.solution = Objects.requireNonNull(solution);
    this.nbThreads = nbThreads;
    super();
    synchronized (lock) {
      state = ".".repeat(solution.length());
      step = Step.INTERRUPTED;
    }
  }

  public static WordGuessCorrection newWordGuess(int nbThreads) {
    var solution = Words.randomWord();
    return new WordGuessCorrection(solution, nbThreads);
  }

  public boolean register() throws InterruptedException {
    synchronized (lock) {
      if(step != Step.REGISTERING){
        return false;
      }
      credits.put(Thread.currentThread(), CREDITS);
      if(credits.size() == nbThreads){
        step = Step.PLAYING;
        notifyAll();
        return true;
      }
      while (step == Step.REGISTERING){
        try {
          lock.wait();
        } catch (InterruptedException e){
          lock.notifyAll();
          step = Step.INTERRUPTED;
          credits.remove(Thread.currentThread());
          return false;
        }
      }
      if(isInterrupted){
        // step = Step.INTERRUPTED;
        throw new InterruptedException();
      }
      return true;
    }
  }

  public Map<Thread, Integer> credits() {
    synchronized (lock) {
      return Map.copyOf(credits);
    }
  }

  private Optional<String> play(char letter) throws InterruptedException {
    synchronized (lock) {
      if(credits.containsKey(Thread.currentThread())){
        throw new IllegalStateException();
      }
      if (step != Step.PLAYING) {
        switch (step) {
          case REGISTERING -> throw new IllegalStateException();
          case WON -> Optional.of(solution);
          case LOST -> Optional.empty();
          default -> throw new AssertionError();
        }
      }
      state = Words.addLetter(letter, state, solution);
      if(!solution.contains("" + letter)) {
        var newCredit = credits.merge(Thread.currentThread(), -1, Integer::sum);
        if(newCredit == 0){
          nbLosers++;
          if(nbLosers == nbThreads){
            step = Step.LOST;
            lock.notifyAll();
            return Optional.empty();
          }
          while(step == Step.PLAYING) {
            lock.wait();
          }
          if(step == Step.WON) {
            return Optional.of(solution); // ou state
          }
          return Optional.empty(); // je suis en Step.LOST
        }
        return Optional.of(state);
      }
      if(Words.isComplete(state)){
        step = Step.WON;
        lock.notifyAll();
        return Optional.of(solution); // ou state ou enlever la ligne
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