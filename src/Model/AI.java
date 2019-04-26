package Model;

import java.util.ArrayList;

public class AI {
    private int level;
    Battle battle = new Battle();

 public Coordinate setCardCoordinates(Card card) {

    }

    public Coordinate setTargetCoordiantes(Card card) {
        if (card instanceof Minion) {
            ArrayList<Card> closestEnemyCards = new ArrayList<>();
            switch (card.getAssaultType()) {
                case MELEE:
                    for (int i = -1; i <2 ; i++) {
                        for (int j = -1; j < 2; j++) {
                            if (battle.getField(card.getCoordinate().getX(),card.getCoordinate().getY()+1).getCardID()!=0) {
                                addEnemy(closestEnemyCards, card.getCoordinate().getX()+i, card.getCoordinate().getY() +j);
                            }
                        }
                    }

                    int leastHp =100;
                    int miratarin =0;
                    for (int i = 0; i < closestEnemyCards.size() ; i++) {
                        if(closestEnemyCards.get(i).getHealthPoint()<leastHp){
                            miratarin =i;
                        }
                        if(closestEnemyCards.get(i) instanceof Hero){
                            return closestEnemyCards.get(i).getCoordinate();
                        }
                    }
                    return closestEnemyCards.get(miratarin).getCoordinate();
                    break;
                case HYBRID:


                    break;
                case RANGED:


                    break;
            }
        }

    }

    private void addEnemy(ArrayList<Card> closestEnemyCards, int x, int y) {
        if (battle.getField()[x][y] != -1) {
            for (int i = 0; i < battle.getFieldCards()[0].length; i++) {
                if (battle.getFieldCards()[0][i] != null && battle.getFieldCards()[0][i].getId() == battle.getField()[x][y] && battle.getFieldCards()[0][i].getCardHolder() == 1) {
                    closestEnemyCards.add(battle.getFieldCards()[0][i]);
                }
            }
        }
    }

    public Coordinate setDestinationCoordinates() {

    }

    public Card chooseCard(ArrayList<Card> cards) {

    }
}
