package org.example;

import model.cards.Card;
import model.cards.Rank;
import model.cards.Suit;
import model.players.*;

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

        // Betting

        // Setup of first round of cards
        List<AbstractPlayer> firstRoundOfDraws = new ArrayList<>(players);
        firstRoundOfDraws.add(dealer);
        firstRoundOfDraws.addAll(players);
        drawAllPlayer(deck, firstRoundOfDraws); // lehetnek a listában duplikációk, minden játékos húz, aztán a dealer húz, aztán megint minden játékos húz

        try(Scanner scanner = new Scanner(System.in)) {
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
            while (dealer.getStatus() == PlayerStatus.PLAYING) {
                dealer.draw(deck);
            }
            System.out.println(dealer);

            // Evaluate:
            for (HumanPlayer player : players) {
                // az AbstractPlayer draw() metódusa kiértékel ( a Hand class getValue() metódusa segítségével)!!!
                // tehát onna megvan a status:
                switch (player.getStatus()){
                    case BUSTED -> System.out.println(player.getName() + " busted, lost their bet!");
                    case SURRENDERED -> System.out.println(player.getName() + " surrendered!");
                    case STANDING -> {
                        if(dealer.getStatus() == PlayerStatus.BUSTED){
                            System.out.println(player.getName() + " won because " + dealer.getName() + " busted!");
                        } else {
                            if(dealer.getHandValue() > player.getHandValue()){
                                System.out.println(player.getName() + "lost because " + dealer.getName() + "has more points.");
                            } else if (dealer.getHandValue() == player.getHandValue()) {
                                System.out.println(player.getName() + "is in tie with " + dealer.getName());
                            } else {
                                System.out.println(player.getName() + "won");
                            }
                        }
                    }
                }
            }

        }

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
            player.draw(deck);
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