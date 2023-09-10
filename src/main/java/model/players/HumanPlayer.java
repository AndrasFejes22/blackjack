package model.players;

import java.util.List;

public class HumanPlayer extends AbstractPlayer{

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public void apply(Action action) {

    }

    /**
     * Surrender:
     * If the player has only the TWO cards remaining after the deal and decides that he cannot win the game, he can give up the game with this announcement
     *
     */

    @Override
    public List<Action> getAvailableActions() {
        if(status != PlayerStatus.PLAYING){// can be static import
            throw new IllegalStateException("There are no actions in " + status + " status!");
        }
        if(hand.getNumberOfCards() == 2){
            return List.of(Action.HIT, Action.STAND, Action.SURRENDER);
        } else {
            return List.of(Action.HIT, Action.STAND);
        }

    }
}
