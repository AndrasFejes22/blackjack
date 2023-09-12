package model.players;


import model.cards.Card;
import model.hand.Hand;

import java.util.List;

public class Dealer extends AbstractPlayer{

    public static final int TARGET_HAND_VALUE = 17;

    public Dealer() {
        super("Bank");
    }

    @Override
    public void draw(List<Card> deck) {
        if(status != PlayerStatus.PLAYING){// can be static import
            throw new IllegalStateException("Cannot draw in " + status + " status!");
        }
        hand.addCard(deck.remove(0));
        int value = hand.getValue();

        if(value >= TARGET_HAND_VALUE){
            status = PlayerStatus.STANDING;
        }

        if(value > Hand.BLACK_JACK_VALUE){ // can be static import
            status = PlayerStatus.BUSTED;
        }
    }

    @Override
    public void apply(Action action, List<Card> deck) {
        throw new UnsupportedOperationException("The bank has internal decision making!");
    }

    @Override
    public List<Action> getAvailableActions() {
        return List.of();
    }

    @Override
    public int getHandValue() {
        return 0;
    }
}
