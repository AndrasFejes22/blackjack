package model.hand;

import model.cards.Card;
import model.cards.Rank;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private final List<Card> cards = new ArrayList<>();
    public static final int BLACK_JACK_VALUE = 21;
    private int bet;

    public Hand(int bet) {
        this.bet = bet;
    }

    public int getBet() {
        return bet;
    }

    public void addCard(Card card){
        cards.add(card);
    }

    // Ace case: A, 9, 9, A -> 20
    public int getValue(){
        int value = 0;
        int numberOfAces = 0;
        for (Card card : cards) {
            if(card.rank() != Rank.ACE){
                value += card.rank().value;
            } else {
                numberOfAces++;
            }
        }
        //System.out.println("numberOfAces: "+numberOfAces);
        for (int i = 0; i < numberOfAces; i++) {
            if(value + Rank.ACE.value > 21){
                value += 1;
            } else {
                value += Rank.ACE.value;
            }
        }

        return value;
    }

    @Override
    public String toString() {
        return cards + " (" + getValue() + ")";
    }

    public int getNumberOfCards(){
        return cards.size();
    }
}
