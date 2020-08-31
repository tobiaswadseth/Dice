package com.tobiaswadseth.dice;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    static Die die;
    static Scanner scanner = new Scanner(System.in);
    static boolean playing = true;
    static List<Game> games = new ArrayList<>();
    static Game game;
    static List<Player> players = new ArrayList<>();
    static int dice = 1;
    static int[] highScores = {0, 0, 0};

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome to Dice JE! 🎲\nBy Tobias Wadseth");
        do {
            game = new Game();
            addHumans();
        } while (playing);
        System.out.println("\nBye bye! 👋");
        scanner.close();
    }

    private static boolean notInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    private static void addHumans() throws InterruptedException {
        players.clear();
        System.out.println("\nPlease enter the amount of human players! 🙎‍");
        String amountString = scanner.nextLine();

        if (notInt(amountString)) {
            addHumans();
            return;
        }

        int amount = Integer.parseInt(amountString);

        if (amount <= 0) {
            System.out.println("\nNo humans have been added!");
            addRobots();
            return;
        }

        for (int i = 0; i < amount; i++) {
            System.out.println("\nPlease enter the name of player #" + (i + 1) + "! ✏️");
            String name = scanner.nextLine();
            Player player = new Player(name);
            game.addPlayer(player);
            players.add(player);
            System.out.println("\nSuccessfully added " + name + "! ✅");
        }
        addRobots();
    }

    private static void addRobots() throws InterruptedException {
        System.out.println("\nPlease enter the amount of robots! 🤖");
        String robotAmountString = scanner.nextLine();

        if (notInt(robotAmountString)) {
            addRobots();
            return;
        }

        int robotAmount = Integer.parseInt(robotAmountString);

        if (robotAmount <= 0) {
            if (players.isEmpty()) {
                System.out.println("\nThere needs to be at least one player!");
                addHumans();
                return;
            }
            System.out.println("\nNo robots have been added!");
            setupDieSide();
            return;
        }

        for (int i = 0; i < robotAmount; i++) {
            UUID uuid = UUID.randomUUID();
            String random = "Robot-" + uuid.toString().split("-")[0];
            Player player = new Player(random);
            game.addPlayer(player);
            players.add(player);
        }

        System.out.println("\nSuccessfully added " + robotAmount + " robots! 🤖");
        setupDieSide();
    }

    public static void setupDieSide() throws InterruptedException {
        System.out.println("\nHow many sides to a single die should there be? 🎲");
        String dieSideAmountString = scanner.nextLine();

        if (notInt(dieSideAmountString)) {
            setupDieSide();
            return;
        }
        int dieSideAmount = Integer.parseInt(dieSideAmountString);

        if (dieSideAmount <= 0) {
            System.out.println("\nThe die needs to have at least 1 side!");
            setupDieSide();
            return;
        }

        die = new Die(dieSideAmount);
        System.out.println("\nThe dice will have " + dieSideAmount + " sides! 🎲");
        setupDieAmount();
    }

    public static void setupDieAmount() throws InterruptedException {
        System.out.println("\nHow many dice should there be? 🎲");
        String dieAmountString = scanner.nextLine();

        if (notInt(dieAmountString)) {
            setupDieAmount();
            return;
        }

        int dieAmount = Integer.parseInt(dieAmountString);

        if (dieAmount <= 0) {
            System.out.println("\nThere needs to be at least 1 die!");
            setupDieAmount();
            return;
        }

        dice = dieAmount;
        System.out.println("\nThere will be " + dieAmount + " dice! 🎲");
        startGame();

    }

    public static void startGame() throws InterruptedException {
        for (Player player : players) {
            System.out.println("\n" + player.getName() + " is rolling...");
            List<Integer> roll = new ArrayList<>();

            for (int i = 0; i < dice; i++) {
                TimeUnit.MILLISECONDS.sleep(500);
                int temp = die.roll();
                roll.add(temp);
                System.out.println("Dice #" + (i + 1) + " has been rolled and it was a " + temp + " | Total sum so far: " + sum(roll));
            }

            game.addRoll(player, roll);
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println(player.getName() + " rolled their dice and got a total sum of: " + sum(roll));
            List<String> temp = new ArrayList<>();
            for (Integer i : roll) {
                temp.add(String.valueOf(i));
            }
            System.out.println("Roll: " + String.join(", ", temp));

            // Update the highScores array and sort it
            if (sum(roll) > highScores[2]) {
                highScores[2] = sum(roll);
                cocktailSort(highScores);
            }

            TimeUnit.MILLISECONDS.sleep(1000);
        }
        endGame();
    }

    public static void endGame() {
        Player winner = game.getWinner();
        System.out.println("\n-----------------------------------------");
        System.out.println("\nThis game's winner 🏆");
        System.out.println("\nName: " + winner.getName() + " | Score: " + sum(game.getRolls().get(winner)));
        System.out.println("\nRoll: " + game.getRolls().get(winner).stream().map(Object::toString).collect(Collectors.joining(", ")));
        System.out.println("\nThis game's scores 📝");
        for (Player player : game.getPlayers()) {
            System.out.println("\nName: " + player.getName() + " | Score: " + sum(game.getRolls().get(player)));
            System.out.println("\nRoll: " + game.getRolls().get(player).stream().map(Object::toString).collect(Collectors.joining(", ")));
        }

        if (!games.isEmpty()) {
            System.out.println("\n-----------------------------------------");
            System.out.println("\nHistory of previous games");
            int i = 1;
            for (Game prev : games) {
                System.out.println("\n-----------------------------------------");
                System.out.println("\nGame " + i);
                System.out.println("\nWinner 🏆");
                System.out.println("\nName: " + prev.getWinner().getName() + " | Score: " + sum(prev.getRolls().get(prev.getWinner())));
                System.out.println("\nRoll: " + prev.getRolls().get(prev.getWinner()).stream().map(Object::toString).collect(Collectors.joining(", ")));
                System.out.println("\nScores 📝");
                for (Player player : prev.getPlayers()) {
                    System.out.println("\nName: " + player.getName() + " | Score: " + sum(prev.getRolls().get(player)));
                    System.out.println("\nRoll: " + prev.getRolls().get(player).stream().map(Object::toString).collect(Collectors.joining(", ")));
                }
                i++;
            }
        }

        System.out.println("\n-----------------------------------------");
        System.out.println("\nHigh Scores");

        int index = 1;
        for (Integer highScore : highScores) {
            String pos = "";
            switch (index) {
                case 1:
                    pos = "🥇";
                    break;
                case 2:
                    pos = "🥈";
                    break;
                case 3:
                    pos = "🥉";
                    break;
            }
            System.out.println("\n" + pos + " " + highScore);
            index++;
        }

        games.add(game);
        players.clear();
        System.out.println("\n-----------------------------------------");
        System.out.println("\nDo you wish to play again? (YES/NO)");
        playing = scanner.nextLine().toUpperCase().startsWith("Y");
    }

    public static int sum(List<Integer> list) {
        int i = 0;
        for (Integer j : list) {
            i += j;
        }
        return i;

    }

    public static void cocktailSort(int[] list) {
        boolean swapped = true;
        int start = 0;
        int end = list.length;

        while (swapped) {
            swapped = false;
            for (int i = start; i < end - 1; ++i) {
                swapped = swap(list, swapped, i);
            }
            if (!swapped) break;

            swapped = false;

            end--;

            for (int i = end - 1; i > start - 1; i--) {
                swapped = swap(list, swapped, i);
            }
            start++;
        }
    }

    private static boolean swap(int[] list, boolean swapped, int i) {
        if (list[i] < list[i + 1]) {
            int temp = list[i];
            list[i] = list[i + 1];
            list[i + 1] = temp;
            swapped = true;
        }
        return swapped;
    }
}

class Player {
    private final String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Game {
    private final List<Player> players;
    private final Map<Player, List<Integer>> rolls;

    public Game() {
        this.players = new ArrayList<>();
        this.rolls = new HashMap<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public Map<Player, List<Integer>> getRolls() {
        return rolls;
    }

    public void addRoll(Player player, List<Integer> roll) {
        this.rolls.put(player, roll);
    }

    public Player getWinner() {
        Player winner = null;
        int max = 0, temp = 0;
        for (Map.Entry<Player, List<Integer>> entry : rolls.entrySet()) {
            for (Integer i : entry.getValue()) {
                temp += i;
            }
            if (temp > max) {
                max = temp;
                winner = entry.getKey();
            }
            temp = 0;
        }
        return winner;
    }
}

class Die {
    private final Integer sides;

    public Die(Integer sides) {
        this.sides = sides;
    }

    public Integer roll() {
        return (int) (1 + Math.random() * sides);
    }
}