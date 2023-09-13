package model.players;

import model.cards.Card;
import model.hand.Hand;

import java.util.List;

public abstract class AbstractPlayer {
    // milyen lap van a kezükben (hand)
    // tét
    // státusz?

    private final String name;
    protected PlayerStatus status = PlayerStatus.PLAYING;
    protected Hand hand = new Hand();

    protected AbstractPlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void draw (List<Card> deck){
        System.out.println("Drawing");
        if(status != PlayerStatus.PLAYING){
            throw new IllegalStateException("Cannot draw in " + status + " status!");
        }
        hand.addCard(deck.remove(0));
        int value = hand.getValue();
        if(value > Hand.BLACK_JACK_VALUE){
            status = PlayerStatus.BUSTED;
        }
        if(value == Hand.BLACK_JACK_VALUE){
            if(hand.getNumberOfCards() == 2) {
                status = PlayerStatus.BLACKJACK;
            } else {
                status = PlayerStatus.STANDING;
            }
        }
    }

    public abstract void apply(Action action, List<Card> deck);
    public abstract List<Action> getAvailableActions();

    public int getHandValue(){
        return hand.getValue();
    }

    @Override
    public String toString() {
       return name + ": " + hand;
    }
}
