package org.example;

import model.cards.Card;
import model.cards.Rank;
import model.cards.Suit;
import model.players.*;
import model.result.RoundResults;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // deck
        List<Card> deck = createDeck();
        Collections.shuffle(deck);

        // Players, Dealer
        Dealer dealer = new Dealer();
        //List<HumanPlayer> players = List.of(new HumanPlayer("Player #1"), new HumanPlayer("Player #2"));
        List<HumanPlayer> players = List.of(new HumanPlayer("Player #1"));



        try(Scanner scanner = new Scanner(System.in)) {
            // Betting
            System.out.println("Please place your bets!");
            System.out.println("(place 0 bet, if you would like to skip this round)");
            for (HumanPlayer player : players) {
                while (true) {
                    try {
                        System.out.printf("%s's bet (1 - %d): ", player.getName(), player.getBudget());
                        String userInput = scanner.nextLine();
                        int bet = Integer.parseInt(userInput);
                        // create hand:
                        player.createHand(bet);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input, please try again!");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage()); //*itt elkapjuk
                    }
                }
            }

            // Setup of first round of cards
            List<AbstractPlayer> firstRoundOfDraws = new ArrayList<>(players);
            firstRoundOfDraws.add(dealer);
            firstRoundOfDraws.addAll(players);
            drawAllPlayer(deck, firstRoundOfDraws); // lehetnek a listában duplikációk, minden játékos húz, aztán a dealer húz, aztán megint minden játékos húz


            // Next round each player draws
            for (HumanPlayer player : players) {
                System.out.println(player);
                while (player.getStatus() == PlayerStatus.PLAYING) {

                    // Actions: (h)it, (s)tand, (su)rrender, etc.
                    List<Action> actions = player.getAvailableActions();
                    System.out.print("Actions: " + getActionLabels(actions) + "? ");
                    String userInput = scanner.nextLine();
                    Optional<Action> selectedAction = findActionByCommand(actions, userInput);
                    if(selectedAction.isPresent()){
                        player.apply(selectedAction.get(), deck);
                        System.out.println(player);
                    } else {
                        System.out.println("Unknown action command!");
                    }
                }
            }
            // Dealer should draw cards
            if(isAnyPlayerIn(players, Set.of(PlayerStatus.BLACKJACK, PlayerStatus.STANDING))) {
                while (dealer.getStatus() == PlayerStatus.PLAYING) {
                    dealer.draw(deck);
                }
            } else {
                System.out.println(dealer.getName() + " skips drawing cards.");
            }
            System.out.println(dealer);

            // Evaluate:
            /**
             * A játék végeredménye
             *
             *     Ha a játékos lapjainak összértéke közelebb van a 21-hez, mint az osztóé, akkor a játékos a tétet 2:1 arányban kapja meg.
             *     Ha az osztó lapjainak összértéke közelebb van a 21-hez, mint a játékosé, akkor a játékos elvesztette a tétet.
             *     Ha a játékos és az osztó lapjainak összértéke egyforma, akkor az állás döntetlen (Push), a megtett tétet visszakapja a játékos.
             *     Ha a játékos lapjainak összértéke a játék során a 21-et meghaladja (Bust), akkor a játékos elvesztette a tétet, az osztó későbbi eredményétől függetlenül.
             *     Ha az osztó lapjainak összértéke a játék során a 21-et meghaladja (Bust), akkor a játékos a tétet 2:1 arányban kapja meg.
             *     Ha a játékos az első két lapjának összértéke pontosan 21 (Blackjack), és az osztó nem Blackjack-et ért el, akkor a játékos a megtett tétet 3:2 arányban kapja meg.
             */
            for (HumanPlayer player : players) {
                RoundResults results = switch (player.getStatus()) {
                    case BUSTED -> new RoundResults(player.getName() + " busted and lost", 0);
                    case SURRENDERED -> new RoundResults(player.getName() + " surrendered", 0.5);
                    case BLACKJACK -> handlePlayerBlackJack(player, dealer);
                    case STANDING -> handlePlayerStanding(player, dealer);
                    case PLAYING -> throw new IllegalStateException(player.getName() + " should not be in " + player.getStatus() + " status");
                    case SKIPPED -> new RoundResults(player.getName() + " skipped this round", 0);
                };
                player.collectReward(results.multiplier());
                System.out.println(results.message());
                System.out.println(player.getName() + "'s budget: " + player.getBudget());
            }

            /*
            for (HumanPlayer player : players) {
                // az AbstractPlayer draw() metódusa kiértékel ( a Hand class getValue() metódusa segítségével)!!!
                // tehát onna megvan a status:
                switch (player.getStatus()){
                    case BUSTED -> System.out.println(player.getName() + " busted, lost their bet!"); // bet: another field
                    case SURRENDERED -> System.out.println(player.getName() + " surrendered!");
                    case BLACKJACK -> {
                        if(dealer.getStatus() == PlayerStatus.BLACKJACK){
                            System.out.println(player.getName() + "lost because " + dealer.getName() + "has BLACKJACK too.");
                        } else {
                            System.out.println(player.getName() + " won because with BLACKJACK!");
                        }
                    }
                    case STANDING -> {
                        if(dealer.getStatus() == PlayerStatus.BUSTED){
                            System.out.println(player.getName() + " won because " + dealer.getName() + " busted!");
                        } else {
                            if(dealer.getHandValue() > player.getHandValue()){
                                System.out.println(player.getName() + "lost because " + dealer.getName() + "has more points.");
                            } else if (dealer.getHandValue() == player.getHandValue()) {
                                System.out.println(player.getName() + "is in tie with " + dealer.getName());
                            } else {
                                System.out.println(player.getName() + " won");
                            }
                        }
                    }
                    case PLAYING -> throw new IllegalStateException(player.getName() + " should not be in " + player.getStatus() + " staus!");
                }
            }
            */

        }

    }

    private static RoundResults  handlePlayerBlackJack(HumanPlayer player, Dealer dealer) {
        String playerName = player.getName();
        if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
            return new RoundResults(playerName + " lost, because " + dealer.getName() + " has BLACKJACK too", 0);
        } else {
            return new RoundResults(playerName + " won with BLACKJACK", 2.5);
        }
    }

    private static RoundResults handlePlayerStanding(HumanPlayer player, Dealer dealer) {
        String playerName = player.getName();
        String dealerName = dealer.getName();
        if (dealer.getStatus() == PlayerStatus.BUSTED) {
            return new RoundResults(playerName + " won, because " + dealerName + " busted", 2);
        }
        if (dealer.getHandValue() > player.getHandValue()) {
            return new RoundResults(playerName + " lost to " + dealerName + " by having less points", 0);
        } else if (dealer.getHandValue() == player.getHandValue()) {
            return new RoundResults(playerName + " is in tie with " + dealerName, 1);
        } else {
            return new RoundResults(playerName + " won", 2);
        }
    }

    private static boolean isAnyPlayerIn(List<HumanPlayer> players, Set<PlayerStatus> desiredStatus) { // Set.of(PlayerStatus.BLACKJACK, PlayerStatus.STANDING))
        for (HumanPlayer player : players) {
            if(desiredStatus.contains(player.getStatus())){
                return true;
            }
        }
        return false;
    }

    private static Optional<Action> findActionByCommand(List<Action> actions, String userInput) {
        for(Action action : actions){
            if(Character.toString(action.command).equalsIgnoreCase(userInput)){
                return Optional.of(action);
            }
        }
        return Optional.empty();
    }

    private static String getActionLabels(List<Action> actions) {
        StringJoiner joiner = new StringJoiner(", ");
        for(Action action : actions){
            joiner.add(action.label);
        }
        return joiner.toString();
    }

    private static void drawAllPlayer(List<Card> deck, List<AbstractPlayer> players){
        for(AbstractPlayer player : players){
            if(player.getStatus() == PlayerStatus.PLAYING) {
                player.draw(deck);
            }
        }
    }

    // "pakli"
    private static List<Card> createDeck(){
        List<Card> deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }
}