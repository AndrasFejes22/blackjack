package model.hand;

import model.cards.Card;
import model.cards.Rank;
import model.cards.Suit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void getValueTest() {


        // case: A, 9, 9, A -> 20
        Card card = new Card(Suit.CLUB, Rank.ACE);
        Card card2 =new Card(Suit.CLUB, Rank.NINE);
        Card card3 =new Card(Suit.CLUB, Rank.NINE);
        Card card4 =new Card(Suit.CLUB, Rank.ACE);

        Hand hand = new Hand(50);
        hand.addCard(card);
        hand.addCard(card2);
        hand.addCard(card3);
        hand.addCard(card4);

        assertEquals(20, hand.getValue());


        // blackjack
        Card card5 = new Card(Suit.CLUB, Rank.TEN);
        Card card6 = new Card(Suit.CLUB, Rank.ACE);

        Hand hand2 = new Hand(50);
        hand2.addCard(card5);
        hand2.addCard(card6);

        assertEquals(21, hand2.getValue());
        // 16?
        Card card7 = new Card(Suit.DIAMOND, Rank.FIVE);
        Card card8 = new Card(Suit.SPADE, Rank.ACE);
        Card card9 = new Card(Suit.HEART, Rank.QUEEN);

        Hand hand3 = new Hand(50);
        hand3.addCard(card7);
        hand3.addCard(card8);
        hand3.addCard(card9);
        assertEquals(16, hand3.getValue());

        /*
        // case: 10, A, A -> 21 (Not real case, after 10 + A --> Stand)
        Card card5 =new Card(Suit.CLUB, Rank.TEN);
        Card card6 =new Card(Suit.CLUB, Rank.ACE);
        Card card7 =new Card(Suit.CLUB, Rank.ACE);
        Hand hand2 = new Hand();
        hand2.addCard(card5);
        hand2.addCard(card6);
        hand2.addCard(card7);
        assertEquals(21, hand2.getValue());

         */



    }
}