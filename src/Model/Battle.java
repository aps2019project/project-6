package Model;

import View.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Battle {
    private Card currentCard;
    private Card targetCard;
    private Coordinate currentCoordinate;
    private Item currentItem;
    private Account[] accounts;
    private Account currentPlayer;
    private Card[][] graveyard = new Card[2][Constants.MAXIMUM_DECK_SIZE + 1];
    private Item[][] collectibles = new Item[2][Constants.MAXIMUM_DECK_SIZE];
    private ArrayList<Item> battleCollectibles = new ArrayList<>();
    private Card[][] playerHands = new Card[2][Constants.MAXIMUM_HAND_SIZE];
    private int turn = 0;
    private Cell[][] field = new Cell[Constants.WIDTH][Constants.LENGTH];
    private BattleMode mode;
    private GameType gameType;
    private Process process;
    private Card[][] fieldCards = new Card[2][Constants.MAXIMUM_DECK_SIZE + 1];
    private int level;
    private Menu menu = Menu.getInstance();
    private Shop shop = Shop.getInstance();
    private Random rand = new Random();
    private Match firstPlayerMatch = new Match();
    private Match secondPlayerMatch = new Match();
    private ArrayList<Flag> flagsOnTheGround = new ArrayList<>();
    private int flagsAppeared = 0;
    private Flag mainFlag = new Flag();
    private static final Battle battle = new Battle();
    private boolean attackMode;
    private boolean isOnSpawn;

    private Battle() {
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public int getLevel() {
        return level;
    }

    public static Battle getInstance() {
        return battle;
    }

    public ArrayList<Flag> getFlagsOnTheGround() {
        return flagsOnTheGround;
    }

    public void setFlagsOnTheGround(ArrayList<Flag> flagsOnTheGround) {
        this.flagsOnTheGround = flagsOnTheGround;
    }

    public Item[][] getCollectibles() {
        return collectibles;
    }

    public Battle(Account[] accounts, GameType gameType, BattleMode mode) {
        this.accounts = accounts;
        this.gameType = gameType;
        this.mode = mode;
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    public void setTargetCard(Card targetCard) {
        this.targetCard = targetCard;
    }

    public void setAccounts(Account... accounts) {
        this.accounts = accounts;
    }


    public void setMode(BattleMode mode) {
        this.mode = mode;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public Message startBattle() {
        System.out.println(battle.accounts.length); //be_deleted
        if (battle.accounts[0] == null || battle.accounts[1] == null) {
            return Message.INVALID_PLAYERS;
        }
        randomizeDeck(0);
        randomizeDeck(1);
        for (int i = 0; i < 5; i++) {
            addToHand(0);
            addToHand(1);
        }
        setManaPoints();
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.LENGTH; j++) {
                this.field[i][j] = new Cell();
            }
        }
        accounts[0].getCollection().getMainDeck().getHero().setCoordinate(new Coordinate(Constants.WIDTH / 2, 0));
        field[Constants.WIDTH / 2][0].setCardID(accounts[0].getCollection().getMainDeck().getHero().getId());
        fieldCards[0][0] = accounts[0].getCollection().getMainDeck().getHero();
        accounts[1].getCollection().getMainDeck().getHero().setCoordinate(new Coordinate(Constants.WIDTH / 2, Constants.LENGTH - 1));
        field[Constants.WIDTH / 2][Constants.LENGTH - 1].setCardID(accounts[1].getCollection().getMainDeck().getHero().getId());
        fieldCards[1][0] = accounts[1].getCollection().getMainDeck().getHero();
        currentPlayer = accounts[0];
        return Message.BATTLE_STARTED;
    }

    public boolean checkForWin() {

        boolean firstPlayerWon = false;
        boolean secondPlayerWon = false;
        switch (mode) {
            case KILLENEMYHERO:
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < fieldCards[i].length; j++) {
                        if (fieldCards[i][j].getType().equals("Hero") && fieldCards[i][j].getHealthPoint() <= 0) {
                            if (i == 0) secondPlayerWon = true;
                            if (i == 1) firstPlayerWon = true;
                        }
                    }
                }
                break;
            case FLAG:
                if (mainFlag.getTurnCounter() >= Constants.TURNS_HOLDING_FLAG) {
                    if (mainFlag.getAccount().equals(accounts[0])) {
                        firstPlayerWon = true;
                    }
                    if (mainFlag.getAccount().equals(accounts[1])) {
                        secondPlayerWon = true;
                    }
                }

                break;
            case COLLECTING:
                for (int i = 0; i < 2; i++) {
                    if (accounts[i].getFlagsCollected() >= Constants.MAXIMUM_FLAGS / 2) {
                        if (i == 1) secondPlayerWon = true;
                        if (i == 0) firstPlayerWon = true;
                    }
                }
                break;
        }
        if (firstPlayerWon) {
            if (secondPlayerWon) {
                firstPlayerMatch.setResult(MatchResult.TIE);
                secondPlayerMatch.setResult(MatchResult.TIE);
                accounts[0].setBudget(accounts[0].getBudget() + 500);
                accounts[1].setBudget(accounts[0].getBudget() + 500);
                setMatchInfo();
                refactorDeck(0);
                refactorDeck(1);
                return true;
            }
            if (setAIStoryAwards()) return true;
            firstPlayerMatch.setResult(MatchResult.WON);
            secondPlayerMatch.setResult(MatchResult.LOST);
            accounts[0].setBudget(accounts[0].getBudget() + 1000);
            setMatchInfo();
            refactorDeck(0);
            refactorDeck(1);
            return true;
        } else if (secondPlayerWon) {
            firstPlayerMatch.setResult(MatchResult.LOST);
            secondPlayerMatch.setResult(MatchResult.WON);
            accounts[1].setBudget(accounts[0].getBudget() + 1000);
            setMatchInfo();
            refactorDeck(0);
            refactorDeck(1);
            return true;

        }
        return false;
    }

    private boolean setAIStoryAwards() {
        if (accounts[1].getName().equals("powerfulAI")) {
            if (mode.equals(BattleMode.KILLENEMYHERO)) {
                accounts[0].setBudget(accounts[0].getBudget() + 500);
            }
            if (mode.equals(BattleMode.FLAG)) {
                accounts[0].setBudget(accounts[0].getBudget() + 1000);
            }
            if (mode.equals(BattleMode.COLLECTING)) {
                accounts[0].setBudget(accounts[0].getBudget() + 1500);
            }
            firstPlayerMatch.setResult(MatchResult.WON);
            setMatchInfo();
            refactorDeck(0);
            refactorDeck(1);
            return true;
        }
        return false;
    }

    public void setMatchInfo() {
        firstPlayerMatch.setTime(LocalDateTime.now());
        secondPlayerMatch.setTime(LocalDateTime.now());
        firstPlayerMatch.setRival(accounts[1].getName());
        secondPlayerMatch.setRival(accounts[0].getName());
    }

    public void resign() {
        if ((turn % 2) == 0) {
            firstPlayerMatch.setResult(MatchResult.LOST);
            secondPlayerMatch.setResult(MatchResult.WON);
            accounts[1].setBudget(accounts[0].getBudget() + 1000);
            setMatchInfo();
        }
        if ((turn % 2) == 1) {
            firstPlayerMatch.setResult(MatchResult.WON);
            secondPlayerMatch.setResult(MatchResult.LOST);
            accounts[0].setBudget(accounts[0].getBudget() + 1000);
            setMatchInfo();
        }
        refactorDeck(0);
        refactorDeck(1);
    }

    public Coordinate getCurrentCoordinate() {
        return currentCoordinate;
    }

    public void setCurrentCoordinate(Coordinate currentCoordinate) {
        this.currentCoordinate = currentCoordinate;
    }

    public Cell getField(int x, int y) {
        return field[x][y];
    }

    public Card[][] getFieldCards() {
        return fieldCards;
    }


    public void showCardInfo(int cardId) {

    }

    public boolean selectCard(int cardId) {
        Card card = Card.getCardByID(cardId, fieldCards[turn % 2]);
        if (card == null) {
            return false;
        }
        currentCard = card;
        currentCoordinate = currentCard.getCoordinate();
        return true;
    }

    public boolean moveTo(Coordinate coordinate) {
        if (currentCard == null)
            return false;
        if (!currentCard.isAbleToMove())
            return false;
        if (coordinate.getX() > 8 || coordinate.getY() > 8 || coordinate.getY() < 0 || coordinate.getX() < 0)
            return false;
        if (currentCard.getCoordinate() == coordinate) {
            currentCard.setAbleToMove(false);
            return true;
        }
        if (Coordinate.getManhattanDistance(currentCard.getCoordinate(), coordinate) > currentCard.getMaxPossibleMoving()) {
            return false;
        }
        field[currentCard.getCoordinate().getX()][currentCard.getCoordinate().getY()].setCardID(0);
        currentCard.setCoordinate(coordinate);
        field[currentCard.getCoordinate().getX()][currentCard.getCoordinate().getY()].setCardID(currentCard.getId());
        if (mode.equals(BattleMode.COLLECTING)) {
            for (Flag flag :
                    flagsOnTheGround) {
                if (currentCard.getCoordinate().equals(flag.getCoordinate())) {
                    collectFlags();
                }
            }
        }
        if (mode.equals(BattleMode.FLAG)) {
            if (currentCard.getCoordinate().equals(mainFlag.getCoordinate())) {
                holdMainFlag();
            }
        }
        currentCard.setAbleToMove(false);
        return true;

    }

    public boolean isAttackable(Card currentCard, Card targetCard) {
        if (targetCard.getBuffs().get(0).getType().equals(BuffType.NEGATIVE_DISPEL)) {
            return false;
        }
        if (targetCard.getBuffs().get(0).getType().equals(BuffType.ASHBUS) && targetCard.getAssaultPower() > currentCard.getAssaultPower()) {
            return false;
        }
        return true;
    }

    public Message attack(int opponentCardId, Card currentCard) {
        targetCard = Card.getCardByID(opponentCardId, fieldCards[(turn + 1) % 2]);
        if (targetCard == null) {
            return Message.INVALID_TARGET;
        }
        if (!isInRange(targetCard, currentCard)) {//&& !accounts[1].getName().equals("powerfulAI")
            return Message.UNAVAILABLE;
        }
        if (!currentCard.isAbleToAttack()) {
            if (targetCard.isAbleToAttack()) {
                return Message.NOT_ABLE_TO_ATTACK;
            } else {
                return Message.NOT_ABLE_TO_ATTACK;
            }
        }
        checkAttackHistory(opponentCardId, currentCard);
        checkOnAttackSpecials(currentCard);
        currentCard.setAbleToAttack(false);
        targetCard.modifyHealth(-currentCard.getAssaultPower());
        if (isAttackable(currentCard, targetCard) && targetCard.getIsHoly() != 0)
            targetCard.setHealthPoint(targetCard.getHealthPoint() - targetCard.getIsHoly());
        attack(currentCard.getId(), targetCard);
        killEnemy(targetCard);
        return null;
    }

    private void checkOnAttackSpecials(Card currentCard) {
        if (currentCard.getBuffs().size() >= 1 && currentCard.getBuffs().get(0).getActivationType().equals(ActivationType.ON_ATTACK)) {
            onAttackSpecialPower();
        }
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public Card getTargetCard() {
        return targetCard;
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public Account getCurrentPlayer() {
        return currentPlayer;
    }

    public Card[][] getGraveyard() {
        return graveyard;
    }

    public Card[][] getPlayerHands() {
        return playerHands;
    }

    public int getTurn() {
        return turn;
    }

    public Cell[][] getField() {
        return field;
    }

    public BattleMode getMode() {
        return mode;
    }

    public GameType getGameType() {
        return gameType;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setManaPoints() {
        if (turn <= 14) {
            accounts[0].setMana((turn / 2) + Constants.INITIAL_MANA);
            accounts[1].setMana((turn / 2) + Constants.INITIAL_MANA + 1);
        } else {
            accounts[0].setMana(Constants.MAX_MANA);
            accounts[1].setMana(Constants.MAX_MANA);
        }
    }

    public boolean spendMana(int mana) {
        if (accounts[turn % 2].getMana() < mana) {
            return false;
        }
        accounts[turn % 2].setMana(accounts[turn % 2].getMana() - mana);
        return true;
    }

    public boolean spellIsReady(Buff buff) {
        if (buff.getTurnCount() > 0) {
            return false;
        }
        return true;
    }

    public void holdMainFlag() {
        if (currentCard.getCoordinate().equals(mainFlag.getCoordinate())) {
            mainFlag.setFlagHolder(currentCard);
            mainFlag.setHeld(true);
        }
        if (mainFlag.isHeld()) {
            mainFlag.setCoordinate(currentCard.getCoordinate());
        }
    }


    private void checkAttackHistory(int opponentCardId, Card currentCard) {
        boolean newMinion = true;
        int emptyCell = -1;

        if (currentCard.getType().equals("Minion")) {
            for (int i = 0; i < 40; i++) {
                if (opponentCardId == currentCard.getAttackCount(opponentCardId)) {
                    (currentCard).setAttackCount(i, 1, (currentCard).getAttackCount(opponentCardId) + 1);
                    newMinion = false;
                    break;
                }
                if ((currentCard).getAttackCount(currentCard.getId()) == 0) {
                    emptyCell = i;
                    break;
                }
            }
            if (newMinion) {
                (currentCard).setAttackCount(emptyCell, 0, opponentCardId);
                (currentCard).setAttackCount(emptyCell, 1, 1);
            }
        }
    }

    private void killEnemy(Card targetCard) {
        if (targetCard != null && targetCard.getHealthPoint() <= 0) {
            if (targetCard.getBuffs().size() == 1 && targetCard.getBuffs().get(0).getActivationType().equals(ActivationType.ON_DEATH) &&
                    targetCard.getBuffs().get(0).getType().equals(BuffType.WEAKNESS)) {
                for (int i = 0; i < fieldCards[(turn + 1) % 2].length; i++) {
                    if (fieldCards[(turn + 1) % 2][i].getType().equals("Hero")) {
                        fieldCards[(turn + 1) % 2][i].modifyHealth(-fieldCards[(turn + 1) % 2][i].getBuffs().get(0).getPower());
                    }
                }
            }
            if (mode.equals(BattleMode.FLAG)) {
                mainFlag.setTurnCounter(0);
                mainFlag.setHeld(false);
            }
            ArrayList<Card> opponentFieldCards = new ArrayList<>(Arrays.asList(fieldCards[(turn + 1) % 2]));
            opponentFieldCards.remove(targetCard);
            for (int i = 0; i < opponentFieldCards.size(); i++) {
                fieldCards[(turn + 1) % 2][i] = opponentFieldCards.get(i);
            }
            fieldCards[(turn + 1) % 2][opponentFieldCards.size()] = null;
        }
    }

    public Message attackCombo(int opponentCardId, Card... cards) {
        targetCard = Card.getCardByID(opponentCardId, fieldCards[(turn + 1) % 2]);
        if (targetCard == null)
            return Message.INVALID_TARGET;
        for (Card card : cards) {
            if (!isInRange(targetCard, card)) {
                return Message.UNAVAILABLE;
            }
        }

        if (!useSpecialPowerForCombo(cards)) {
            return Message.NOT_ABLE_TO_ATTACK;
        }
        for (Card card : cards) {
            attack(opponentCardId, card);
        }
        return null;
    }

    public boolean isInRange(Card targetCard, Card currentCard) {
        if (targetCard == null || currentCard == null) {
            return false;
        }
        if (Coordinate.getManhattanDistance(targetCard.getCoordinate(), currentCard.getCoordinate())
                > currentCard.getMaxRange() ||
                Coordinate.getManhattanDistance(targetCard.getCoordinate(), currentCard.getCoordinate())
                        < currentCard.getMinRange())
            return false;
        if (currentCard instanceof Minion && currentCard.getRangeType().equals(RangeType.MELEE)) {
            if (Math.abs(currentCard.getCoordinate().getY() - targetCard.getCoordinate().getY()) > 1 ||
                    Math.abs(currentCard.getCoordinate().getX() - targetCard.getCoordinate().getX()) > 1) {
                return false;
            }
        }
        return true;
    }

    public Message holifyCell(Coordinate coordinate) {
        Cell cell = field[coordinate.getX()][coordinate.getY()];
        if (!cell.isHoly()) {
            cell.setHoly(true);
            cell.setHolyTurn(3);
            return null;
        } else {
            return Message.INVALID_TARGET;
        }
    }

    public Message poisonCell(Coordinate coordinate) {
        Cell cell = field[coordinate.getX()][coordinate.getY()];
        if (!cell.isPoison()) {
            cell.setPoison(true);
            cell.setPoisonTurn(2);
            return null;
        } else {
            return Message.INVALID_TARGET;
        }
    }

    public Message fireCell(Coordinate coordinate) {
        Cell cell = field[coordinate.getX()][coordinate.getY()];
        if (!cell.isFire()) {
            cell.setFire(true);
            cell.setFireTurn(1);
            return null;
        } else {
            return Message.INVALID_TARGET;
        }
    }

    public boolean useSpecialPowerForCombo(Card... cards) {
        for (Card card : cards) {
            if (card.getBuffs().size() == 1 && !card.getBuffs().get(0).getType().equals(BuffType.COMBO)) {
                return false;
            }
        }
        return true;
    }

    public Message validSpecialPower() {

        if (getField(currentCoordinate.getX(), currentCoordinate.getY()).getCardID() == 0)
            return Message.INVALID_TARGET;
        Card card = Card.getCardByID(getField(currentCoordinate.getX(), currentCoordinate.getY()).getCardID(), fieldCards[turn % 2]);
        if (card == null)
            return Message.OBJECT_NOT_FOUND;
        if (card.getBuffs().size() == 0) {
            return Message.NOT_ABLE_TO_ATTACK;
        }
        useSpecialPower(card, card.getBuffs().get(0));
        return Message.NULL;
    }

    private void onAttackSpecialPower() {

        for (int i = 0; i < 2; i++) {
            switch (currentCard.getBuffs().get(i).getType()) {
                case CHAMPION:
                    int multiply = currentCard.getAttackCount(targetCard.getId()) * 5;
                    targetCard.modifyHealth(-multiply);
                    break;
                case DISARM:
                    if (targetCard.getBuffs().size() == 1 && !targetCard.getBuffs().get(0).getType().equals(BuffType.NEGATIVE_DISARM)) {
                        targetCard.setAbleToAttack(false);
                        targetCard.addToBuffs(currentCard.getBuffs().get(i));
                    }
                    break;
                case POISON:
                    if (targetCard.getBuffs().size() == 1 && !targetCard.getBuffs().get(0).getType().equals(BuffType.NEGATIVE_POISON)) {
                        targetCard.addToBuffs(currentCard.getBuffs().get(1));
                    }
                    break;
                case LION_ROAR:
                    for (Buff buff : targetCard.getCastedBuffs()) {
                        if (buff.getType().equals(BuffType.HOLY) && buff.getPower() > 0) {
                            targetCard.modifyHealth(1);
                        }
                    }
                    break;
                case WEAKNESS:
                case WHITE_WALKER_WOLF:
                case STUN:
                    targetCard.addToBuffs(currentCard.getBuffs().get(0));
                    break;
                case POSITIVE_DISPEL:
                    for (Buff buff : targetCard.getCastedBuffs()) {
                        if (buff.getType().equals(BuffType.HOLY) || buff.getType().equals(BuffType.POWER)) {
                            targetCard.removeFromBuffs(buff);
                        }
                    }
                    break;
            }
        }

    }

    private void useSpecialPower(Card card, Buff buff) {
        if (card.getType().equals("Hero")) {
            useHeroSP(card, currentCoordinate);
            return;
        }
/*
        if (card.getType().equals("Spell")) {
            useSpell(card, currentCoordinate);
            return;
        }
*/
        int r;
        switch (buff.getType()) {
            case HOLY:
                card.addToBuffs(buff);
                break;
            case STUN:
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (getField(card.getCoordinate().getX() + i, card.getCoordinate().getY() + j).getCardID() != 0) {
                            Card target = Card.getCardByID(getField(card.getCoordinate().getX() + i,
                                    card.getCoordinate().getY() + j).getCardID(), fieldCards[(turn + 1) % 2]);
                            if (target != null) {
                                target.addToBuffs(card.getBuffs().get(0));
                            }
                        }
                    }
                }
                break;
            case BWITCH:
                card.addToBuffs(card.getBuffs().get(0));
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (getField(card.getCoordinate().getX() + i, card.getCoordinate().getY() + j).getCardID() != 0) {
                            Card target = Card.getCardByID(getField(card.getCoordinate().getX() + i,
                                    card.getCoordinate().getY() + j).getCardID(), fieldCards[turn % 2]);
                            if (target != null) {
                                targetCard.setAssaultPower(card.getAssaultPower() + buff.getPower());
                            }
                        }
                    }
                }
                break;
            case JEN_JOON:
                for (int i = 0; i < fieldCards[turn % 2].length; i++) {
                    if (!(fieldCards[turn % 2][i].getType().equals("Hero"))) {
                        fieldCards[turn % 2][i].addToBuffs(card.getBuffs().get(0));
                    }
                }
                break;
            case POWER:
                //OGHAB
                card.addToBuffs(card.getBuffs().get(0));
                break;
            case WEAKNESS:
                for (int i = 0; i < fieldCards[(turn + 1) % 2].length; i++) {
                    if (fieldCards[(turn + 1) % 2][i].getType().equals("Hero")) {
                        fieldCards[(turn + 1) % 2][i].addToBuffs(buff);
                    }
                }
                break;
            case ON_DEATH_WEAKNESS:
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        Card target = Card.getCardByID(getField(card.getCoordinate().getX() + i,
                                card.getCoordinate().getY() + j).getCardID(), fieldCards[turn % 2]);
                        if (target.getType().equals("Minion")) {
                            target.addToBuffs(card.getBuffs().get(0));
                        }
                    }
                }
                break;
            case HOLY_WEAKNESS:
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (Coordinate.getManhattanDistance(field[i][j].getCoordinate(), card.getCoordinate()) <= 2
                                && Coordinate.getManhattanDistance(field[i][j].getCoordinate(), card.getCoordinate()) != 0
                                && field[i][j].getCardID() != 0) {
                            Card target = Card.getCardByID(field[i][j].getCardID(), fieldCards[turn % 2]);
                            assert target != null;
                            target.addToBuffs(card.getBuffs().get(0));
                        }
                    }
                }
                break;
            case ON_SPAWN_WEAKNESS:
                if (fieldCards[(turn + 1) % 2].length >= 2) {
                    r = rand.nextInt(fieldCards[(turn + 1) % 2].length);
                    r += 1;
                    Card target = Card.getCardByID(r, fieldCards[(turn + 1) % 2]);
                    assert target != null;
                    if (target.getType().equals("Minion")) {
                        targetCard.modifyHealth(buff.getPower());
                    }
                }
                break;
        }
        useSpecialPower(card, card.getBuffs().get(1));
    }

    public Message insertCard(Coordinate coordinate, String cardName) {
        boolean validTarget = false;
        for (int i = 0; i < Constants.MAXIMUM_HAND_SIZE; i++) {
            if (playerHands[turn % 2][i].getName().equals(cardName)) {
                Card insert = Card.getCardByName(cardName, playerHands[turn % 2]);
                if (coordinate.getX() > 8 || coordinate.getY() > 8 || coordinate.getX() < 0 || coordinate.getY() < 0)
                    return Message.INVALID_TARGET;
                for (Card card : fieldCards[turn % 2]) {
                    try {
                        if (Coordinate.getManhattanDistance(card.getCoordinate(), coordinate) <= 1) {
                            validTarget = true;
                            break;
                        }
                    } catch (NullPointerException e) {
                    }
                }
                if (!validTarget) {
                    return Message.INVALID_TARGET;
                }
                if (insert != null && !spendMana(insert.getManaPoint())) {
                    return Message.INSUFFICIENT_MANA;
                }
                assert insert != null;
                if (insert.isClass("Minion")) {
                    if (field[coordinate.getX()][coordinate.getY()].getCardID() != 0)
                        return Message.FULL_CELL;
                    field[coordinate.getX()][coordinate.getY()].setCardID(insert.getId());
                    insert.setCoordinate(coordinate);
                    playerHands[turn % 2] = Card.removeFromArray(playerHands[turn % 2], insert);
                    fieldCards[turn % 2] = Card.addToArray(fieldCards[turn % 2], insert);
                    return Message.SUCCESSFUL_INSERT;
                } else if (insert.isClass("Spell")) {
                    if (useSpell(insert, coordinate)) {
                        playerHands[turn % 2] = Card.removeFromArray(playerHands[turn % 2], insert);
                        return Message.SUCCESSFUL_INSERT;
                    }
                    return Message.INVALID_TARGET;
                }
            }

        }

        return Message.NOT_IN_HAND;
    }

    public void endTurn() {
        setAbleToAttackForHeros();
        buffTurnEnd();
        deholifyCell();
        if (mode.equals(BattleMode.COLLECTING) && (turn % Constants.ITEM_APPEARANCE) == 1) {
            flagAppearance();
        }
        if (mode.equals(BattleMode.FLAG)) {
            if (mainFlag.isHeld()) {
                mainFlag.setTurnCounter(mainFlag.getTurnCounter() + 1);
            }
        }
        addToHand(turn % 2);
        turn++;
        setManaPoints();
        currentPlayer = this.accounts[turn % 2];
        currentCard = null;
        targetCard = null;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < fieldCards[i].length; j++) {
                try {
                    for (Buff buff : fieldCards[i][j].getCastedBuffs()) {
                        if (buff.getType() == BuffType.STUN)
                            return;
                    }
                    fieldCards[i][j].setAbleToMove(true);


                } catch (NullPointerException e) {

                }
                try {
                    for (Buff buff : fieldCards[i][j].getCastedBuffs()) {
                        if (buff.getType() == BuffType.DISARM)
                            return;
                    }
                    fieldCards[i][j].setAbleToAttack(true);
                } catch (NullPointerException e) {

                }
            }
        }
    }

    private void setAbleToAttackForHeros() {
        if (turn == 1) {
            for (int i = 0; i < fieldCards.length; i++) {
                if (fieldCards[0][i] instanceof Hero) {
                    fieldCards[0][i].setAbleToAttack(true);
                }
            }
        }
        if (turn == 2) {
            for (int i = 0; i < fieldCards[1].length; i++) {
                if (fieldCards[1][i] instanceof Hero) {
                    fieldCards[1][i].setAbleToAttack(true);
                }
            }
        }
    }

    public void buffTurnEnd() {
        for (int i = 0; i < 2; i++) {
            for (Card card : fieldCards[i]) {
                try {
                    for (Buff buff : card.getCastedBuffs()) {
                        if ((buff.getType().equals(BuffType.NEGATIVE_DISPEL)) && (buff.getType().equals(BuffType.DISARM)
                                || buff.getType().equals(BuffType.WEAKNESS) || buff.getType().equals(BuffType.POISON)
                                || buff.getType().equals(BuffType.STUN))) {
                            card.removeFromBuffs(buff);
                            card.setAbleToMove(true);
                            card.setAbleToAttack(true);
                            card.setAssaultPower(card.getOriginalAssaultPower());

                        }
                        if (buff.getTurnCount() > 0) {
                            buff.setTurnCount(buff.getTurnCount() - 1);
                        }
                        checkForStun(card, buff);
                        checkForDisarm(card, buff);
                        checkForPoison(card, buff);
                        checkForPower(card, buff);
                        checkWhiteWolf(card, buff);
                        checkForWeakness(card, buff);
                        checkForHoly(card, buff);
                        checkForJen(card, buff);
                        if (buff.getTurnCount() == 0) {
                            card.removeFromBuffs(buff);
                        }
                    }
                } catch (NullPointerException e) {
                }
            }
        }
    }

    private void checkForJen(Card card, Buff buff) {
        if (buff.getType().equals(BuffType.JEN_JOON) && buff.getTurnCount() == -1) {
            card.setAssaultPower(card.getAssaultPower() + buff.getPower());
        }
    }

    private void checkForWeakness(Card card, Buff buff) {
        if ((buff.getType().equals(BuffType.WEAKNESS)) && buff.getTurnCount() % 2 == 1 && !buff.getActivationType().equals(ActivationType.ON_DEATH)) {
            card.modifyHealth(0);
            buff.setPower(buff.getPower());
        }
        if ((buff.getType().equals(BuffType.WEAKNESS)) && buff.getTurnCount() % 2 == 1 && buff.getActivationType().equals(ActivationType.ON_DEATH)) {
            targetCard.modifyHealth(buff.getPower());
        }
        if (((buff.getType().equals(BuffType.ON_DEATH_WEAKNESS)) || (buff.getType().equals(BuffType.HOLY_WEAKNESS)))
                && buff.getTurnCount() != 0) {
            targetCard.modifyHealth(buff.getPower());
        }
    }

    private void checkWhiteWolf(Card card, Buff buff) {
        if (buff.getType().equals(BuffType.WHITE_WALKER_WOLF)) {
            card.modifyHealth(buff.getPower());
            buff.setPower(4);
        }
    }

    private void checkForPoison(Card card, Buff buff) {
        if (buff.getType().equals(BuffType.POISON) && buff.getTurnCount() % 2 == 0) {
            card.modifyHealth(-buff.getPower());
        }
    }

    private void checkForDisarm(Card card, Buff buff) {
        if (buff.getType().equals(BuffType.DISARM) && buff.getTurnCount() == 0) {
            card.setAbleToAttack(true);
        }
    }

    private void checkForHoly(Card card, Buff buff) {
        if (buff.getType().equals(BuffType.HOLY) && buff.getTurnCount() != 0 && buff.getTurnCount() % 2 == 0) {
            card.setIsHoly(buff.getPower());
        }
        if (buff.getType().equals(BuffType.HOLY) && buff.getTurnCount() == 0) {
            card.setIsHoly(0);
        }
    }

    private void checkForPower(Card card, Buff buff) {
        if (buff.getType().equals(BuffType.POWER) && buff.getTurnCount() != 0) {
            card.setAssaultPower(card.getAssaultPower() + buff.getPower());
        }
        if (buff.getType().equals(BuffType.POWER) && buff.getTurnCount() == 0) {
            card.setAssaultPower(card.getOriginalAssaultPower());
        }
    }

    private void checkForStun(Card card, Buff buff) {
        if (buff.getType().equals(BuffType.STUN) && buff.getTurnCount() != 0) {
            card.setAbleToAttack(false);
            card.setAbleToMove(false);
        }
        if (buff.getType().equals(BuffType.STUN) && buff.getTurnCount() == 0) {
            card.setAbleToMove(true);
            card.setAbleToAttack(true);
        }
    }

    public void deholifyCell() {
        for (int i = 0; i < Constants.WIDTH; i++) { //deholify cells
            for (int j = 0; j < Constants.LENGTH; j++) {
                try {
                    if (field[i][j].isHoly()) {
                        field[i][j].setHolyTurn(field[i][j].getHolyTurn() - 1);
                        if (field[i][j].getHolyTurn() == 0) {
                            field[i][j].setHoly(false);
                        }
                    }

                } catch (NullPointerException e) {
                }
            }

        }
    }

    public void collectFlags() {
        for (int i = 0; i < flagsOnTheGround.size(); i++) {
            if (Coordinate.getManhattanDistance(flagsOnTheGround.get(i).getCoordinate(), currentCard.getCoordinate()) == 0) {
                accounts[turn % 2].setFlagsCollected(accounts[turn % 2].getFlagsCollected() + 1);
                flagsOnTheGround.remove(flagsOnTheGround.get(i));
                checkForWin();
                return;
            }
        }
    }


    public void flagAppearance() {
        boolean ableToAddFlag = true;
        while (ableToAddFlag) {
            int randomX = rand.nextInt(Constants.WIDTH);
            int randomY = rand.nextInt(Constants.LENGTH);
            if (field[randomX][randomY].getCardID() == 0 && flagsAppeared < Constants.MAXIMUM_FLAGS) {
                Flag flag = new Flag();
                flag.setCoordinate(new Coordinate(randomX, randomY));
                flagsOnTheGround.add(flag);
                ableToAddFlag = false;
                flagsAppeared++;
            }
        }
    }

    public Flag getMainFlag() {
        return mainFlag;
    }

    public void setMainFlag(Flag mainFlag) {
        this.mainFlag = mainFlag;
    }

    public ArrayList<Item> chooseCollectibleItems(ArrayList<Item> items) {
        ArrayList<Item> newItemList = new ArrayList<>();
        for (Item item : items) {
            if (item.getPrice() == 0) {
                newItemList.add(item);
            }
        }
        return newItemList;
    }

    public boolean selectCollectibleId(int collectibleId) {
        Item item = Item.getItemByID(collectibleId, collectibles[turn % 2]);
        if (item == null) {
            return false;
        }
        currentItem = item;
        return true;

    }

    public void enterGraveyard() {
        menu.setStat(MenuStat.GRAVEYARD);
    }

    public boolean useHeroSP(Card hero, Coordinate target) {
        if (!spendMana(hero.getPrice())) {
            return false;
        }
        for (Buff buff :
                hero.getBuffs()) {
            if (!spellIsReady(buff)) {
                return false;
            }
            switch (buff.getSide()) {
                case COMRADE:
                    if (buff.getTargetType().equals("Hero")) {
                        applyBuff(buff, hero);
                        return true;
                    }
                    break;
                case ENEMY:
                    switch (buff.getActivationType()) {
                        case ON_ATTACK:
                            applyBuff(buff, targetCard);
                            return true;
                        default:
                            switch (buff.getEffectArea().get(0).getX()) {
                                case -1:
                                    if (buff.getTargetType().equals("Minion")) {
                                        for (Card card :
                                                fieldCards[(turn + 1) % 2]) {
                                            if (card.getType().equals("Minion")) {
                                                card.addToBuffs(buff);
                                                applyBuff(buff, card);
                                                return true;
                                            }
                                        }
                                    }
                                    break;
                                case 0:
                                    if (buff.getTargetType().equals("Minion")) {
                                        for (Card card :
                                                fieldCards[(turn + 1) % 2]) {
                                            if (card.getType().equals("Minion") && card.getCoordinate().sum(buff.getEffectArea().get(0)).equals(target)) {
                                                card.addToBuffs(buff);
                                                applyBuff(buff, card);
                                                return true;
                                            }
                                        }

                                    }
                                    if (buff.getTargetType().equals("Cell")) {
                                        for (int i = 0; i < Constants.LENGTH; i++) {
                                            for (int j = 0; j < Constants.WIDTH; j++) {
                                                if (i == target.getX() && j == target.getY()) {
                                                    field[i][j].setHoly(true);
                                                    field[i][j].setHolyTurn(3);
                                                    return true;
                                                }
                                            }

                                        }

                                    }
                                    break;
                                case Constants.ROW:
                                    if (buff.getTargetType().equals("Card")) {
                                        for (Card card :
                                                fieldCards[(turn + 1) % 2]) {
                                            if (card.getCoordinate().getX() == hero.getCoordinate().getX()) {
                                                card.addToBuffs(buff);
                                                applyBuff(buff, card);
                                                return true;
                                            }
                                        }

                                    }
                                    if (buff.getTargetType().equals("Minion")) {
                                        for (Card card :
                                                fieldCards[(turn + 1) % 2]) {
                                            if (card.getType().equals("Minion") && card.getCoordinate().getX() == hero.getCoordinate().getX()) {
                                                card.addToBuffs(buff);
                                                applyBuff(buff, card);
                                                return true;
                                            }
                                        }

                                    }
                                    break;
                            }
                    }

            }
        }
        return false;
    }

    public boolean useSpell(Card spell, Coordinate target) {
        for (Buff buff : spell.getBuffs()) {
            switch (buff.getEffectArea().get(0).getX()) {
                case 0:
                    for (Coordinate coordinate : buff.getEffectArea()) {
                        switch (buff.getSide()) {
                            case COMRADE:
                                for (Card card : fieldCards[turn % 2]) {
                                    try {
                                        if (checkForValidSpellTargetSpecific(card, buff, target, coordinate)) {
                                            applyBuff(buff, card);
                                            return true;
                                        }
                                    } catch (NullPointerException e) {
                                    }
                                }
                                break;
                            case ENEMY:
                                for (Card card : fieldCards[(turn + 1) % 2]) {
                                    try {
                                        if (checkForValidSpellTargetSpecific(card, buff, target, coordinate)) {
                                            applyBuff(buff, card);
                                            return true;
                                        }
                                    } catch (NullPointerException e) {
                                    }
                                }
                                break;
                            default:
                                for (Card[] cards : fieldCards) {
                                    for (Card card : cards) {
                                        try {
                                            if (checkForValidSpellTargetSpecific(card, buff, target, coordinate)) {
                                                applyBuff(buff, card);
                                                return true;
                                            }
                                        } catch (NullPointerException e) {

                                        }
                                    }
                                }
                        }
                    }
                    break;
                case Constants.ROW:
                    switch (buff.getSide()) {
                        case COMRADE:
                            for (Card card : fieldCards[turn % 2]) {
                                try {
                                    if (checkForValidSpellTargetY(card, buff, target)) {
                                        applyBuff(buff, card);
                                        return true;
                                    }
                                } catch (NullPointerException e) {

                                }
                            }
                            break;
                        case ENEMY:
                            for (Card card : fieldCards[(turn + 1) % 2]) {
                                try {
                                    if (checkForValidSpellTargetY(card, buff, target)) {
                                        applyBuff(buff, card);
                                        return true;
                                    }
                                } catch (NullPointerException e) {

                                }
                            }
                            break;
                        default:
                            for (Card[] cards : fieldCards) {
                                for (Card card : cards) {
                                    try {
                                        if (checkForValidSpellTargetY(card, buff, target)) {
                                            applyBuff(buff, card);
                                            return true;
                                        }
                                    } catch (NullPointerException e) {

                                    }
                                }
                            }

                    }
                    break;
                case Constants.COLUMN:
                    switch (buff.getSide()) {
                        case COMRADE:
                            for (Card card : fieldCards[turn % 2]) {
                                try {
                                    if (checkForValidSpellTargetX(card, buff, target)) {
                                        applyBuff(buff, card);
                                        return true;
                                    }
                                } catch (NullPointerException e) {
                                }
                            }
                            break;
                        case ENEMY:
                            for (Card card : fieldCards[(turn + 1) % 2]) {
                                try {
                                    if (checkForValidSpellTargetX(card, buff, target)) {
                                        applyBuff(buff, card);
                                        return true;
                                    }
                                } catch (NullPointerException e) {
                                }
                            }
                            break;
                        default:
                            for (Card[] cards : fieldCards) {
                                for (Card card : cards) {
                                    try {
                                        if (checkForValidSpellTargetX(card, buff, target)) {
                                            applyBuff(buff, card);
                                            return true;
                                        }
                                    } catch (NullPointerException e) {

                                    }
                                }
                            }
                    }
                    break;
                case Constants.ALL_FIELD:
                    switch (buff.getSide()) {
                        case COMRADE:
                            for (Card card : fieldCards[turn % 2]) {
                                try {
                                    if (checkForValidSpellTarget(card, buff)) {
                                        applyBuff(buff, card);
                                        return true;
                                    }
                                } catch (NullPointerException e) {

                                }
                            }
                            break;
                        case ENEMY:
                            for (Card card : fieldCards[(turn + 1) % 2]) {
                                try {
                                    if (checkForValidSpellTarget(card, buff)) {
                                        applyBuff(buff, card);
                                        return true;
                                    }
                                } catch (NullPointerException e) {

                                }
                            }
                            break;
                        default:
                            for (Card[] cards : fieldCards) {
                                for (Card card : cards) {
                                    try {
                                        if (checkForValidSpellTarget(card, buff)) {
                                            applyBuff(buff, card);
                                            return true;
                                        }
                                    } catch (NullPointerException e) {

                                    }
                                }
                            }
                    }
                    break;
            }
        }
        return false;
    }

    public void applyBuff(Buff buff, Card card) {
        buff.setTurnCount(buff.getTurnCount() - 1);
        card.getCastedBuffs().add(buff);
        switch (buff.getType()) {
            case POISON:
                card.modifyHealth(buff.getPower());
            case STUN:
                card.setAbleToAttack(false);
                card.setAbleToMove(false);
                break;
            case DISARM:
                card.setAbleToCounter(false);
                break;
            case HIT_POWER:
                card.modifyHit(buff.getPower());
                break;
            case HEALTH_POWER:
                card.modifyHealth(buff.getPower());
                break;
            case HIT_WEAKNESS:
                card.modifyHit(buff.getPower());
                break;
            case HEALTH_WEAKNESS:
                card.modifyHealth(buff.getPower());
                break;
        }
    }

    private boolean checkForValidSpellTargetSpecific(Card card, Buff buff, Coordinate target, Coordinate coordinate) {
        if (checkForValidSpellTarget(card, buff)) {
            if (card.getCoordinate().equals(target.sum(coordinate))) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForValidSpellTargetY(Card card, Buff buff, Coordinate target) {
        if (checkForValidSpellTarget(card, buff)) {
            if (card.getCoordinate().getY() == target.getY()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForValidSpellTargetX(Card card, Buff buff, Coordinate target) {
        if (checkForValidSpellTarget(card, buff)) {
            if (card.getCoordinate().getX() == target.getX()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForValidSpellTarget(Card card, Buff buff) {
        if (card.isClass(buff.getTargetType()) || buff.getTargetType().equals("Card")) {
            return true;
        }
        return false;
    }

    //******************************************************************************************************************
    //AI FUNCTIONS BELOW

    public Coordinate setCardCoordinates() {
        for (int i = 0; i < fieldCards[1].length; i++) {
            if (fieldCards[1][i] != null) {
                if (field[fieldCards[1][i].getCoordinate().getX() + 1][fieldCards[1][i].getCoordinate().getY()].getCardID() == 0) {
                    return new Coordinate(fieldCards[1][i].getCoordinate().getX() + 1, fieldCards[1][i].getCoordinate().getY());
                }
                if (field[fieldCards[1][i].getCoordinate().getX() - 1][fieldCards[1][i].getCoordinate().getY()].getCardID() == 0) {
                    return new Coordinate(fieldCards[1][i].getCoordinate().getX() - 1, fieldCards[1][i].getCoordinate().getY());
                }
                if (field[fieldCards[1][i].getCoordinate().getX()][fieldCards[1][i].getCoordinate().getY() - 1].getCardID() == 0) {
                    return new Coordinate(fieldCards[1][i].getCoordinate().getX() + 1, fieldCards[1][i].getCoordinate().getY() - 1);
                }
                if (field[fieldCards[1][i].getCoordinate().getX()][fieldCards[1][i].getCoordinate().getY() + 1].getCardID() == 0) {
                    return new Coordinate(fieldCards[1][i].getCoordinate().getX(), fieldCards[1][i].getCoordinate().getY() + 1);
                }
            }
        }
        return null;
    }

    public Coordinate setTargetCoordinates(Card card) {
        if (card.getType().equals("Minion")) {
            ArrayList<Card> closestEnemyCards = new ArrayList<>();
            switch (card.getAssaultType()) {
                case MELEE:
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            if (getField(card.getCoordinate().getX(), card.getCoordinate().getY() + 1).getCardID() != 0) {
                                addEnemy(closestEnemyCards, card.getCoordinate().getX() + i, card.getCoordinate().getY() + j);
                            }
                        }
                    }

                    int miratarin = getMiratarin(closestEnemyCards);
                    for (Card closestEnemyCard : closestEnemyCards) {
                        if (closestEnemyCard.getType().equals("Hero")) {
                            return closestEnemyCard.getCoordinate();
                        }
                    }
                    return closestEnemyCards.get(miratarin).getCoordinate();
                case RANGED:
                case HYBRID:
                    for (int i = -card.getMaxRange(); i <= card.getMaxRange(); i++) {
                        for (int j = -card.getMaxRange(); j <= card.getMaxRange(); j++) {
                            if ((i + j <= card.getMaxRange() && (card.getAssaultType().equals(AssaultType.HYBRID)))
                                    || (i + j <= card.getMaxRange() && (card.getAssaultType().equals(AssaultType.RANGED) && i + j != 1))) {
                                if (getField(card.getCoordinate().getX(), card.getCoordinate().getY() + 1).getCardID() != 0) {
                                    addEnemy(closestEnemyCards, card.getCoordinate().getX() + i, card.getCoordinate().getY() + j);
                                }
                            }
                        }
                    }

                    int miratarinn = getMiratarin(closestEnemyCards);
                    for (Card closestEnemyCard : closestEnemyCards) {
                        if (closestEnemyCard.getType().equals("Hero")) {
                            return closestEnemyCard.getCoordinate();
                        }
                    }
                    return closestEnemyCards.get(miratarinn).getCoordinate();

            }
        }
        return null;

    }


    private int getMiratarin(ArrayList<Card> closestEnemyCards) {
        int leastHp = 100;
        int miratarin = 0;
        for (int i = 0; i < closestEnemyCards.size(); i++) {
            if (closestEnemyCards.get(i).getHealthPoint() < leastHp) {
                miratarin = i;
            }
        }
        return miratarin;
    }

    private void addEnemy(ArrayList<Card> closestEnemyCards, int x, int y) {
        if (getField()[x][y].getCardID() != -1) {
            for (int i = 0; i < getFieldCards()[0].length; i++) {
                if (getFieldCards()[0][i] != null && getFieldCards()[0][i].getId() == getField()[x][y].getCardID() && getFieldCards()[0][i].getCardHolder() == 1) {
                    closestEnemyCards.add(getFieldCards()[0][i]);
                }
            }
        }
    }

    public Coordinate setDestinationCoordinate(Card card) {
        switch (mode) {
            case COLLECTING:
                return setDestinationCoordinationModeThree(card);
            case FLAG:
                return setDestinationCoordinatesModeTwo(card);
            case KILLENEMYHERO:
                return setDestinationCoordinatesModeOne(card);
        }
        return null;
    }

    private boolean checkForDevilExistence(Coordinate coordinate) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < fieldCards[i].length; j++) {
                if (coordinationEquality(fieldCards[i][j].getCoordinate(), coordinate)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean coordinationEquality(Coordinate c1, Coordinate c2) {
        return c1.getY() == c2.getY() && c2.getX() == c1.getX();
    }

    private Coordinate makeNewCoordinate(int x, int y) {
        return new Coordinate(x, y);
    }

    private boolean checkCardEquality(Card c1, Card c2) {
        return c1.getName().equals(c2.getName()) && c1.getId() == c2.getId();
    }

    //holdFlag
    private Coordinate setDestinationCoordinatesModeTwo(Card card) {
        //agar flag dasteshe
        if (checkCardEquality(mainFlag.getFlagHolder(), card)) {
            if (card.getCoordinate().getX() <= 6 &&
                    !checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY())) &&
                    !checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX() + 2, card.getCoordinate().getY()))) {
                return validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 2, card.getCoordinate().getY()));
            }
            if (card.getCoordinate().getX() <= 7 && card.getCoordinate().getY() >= 1 &&
                    !checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX() + 1,
                            card.getCoordinate().getY() - 1)) &&
                    (!checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY()))
                            || !checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX(), card.getCoordinate().getY() - 1)))) {
                return validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() - 1));
            }
            if (card.getCoordinate().getX() <= 7 &&
                    card.getCoordinate().getY() <= 3 &&
                    !checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX() + 1,
                            card.getCoordinate().getY() + 1)) &&
                    (!checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY()))
                            || !checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX(), card.getCoordinate().getY() + 1)))) {
                return validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() + 1));
            }
            if (card.getCoordinate().getX() <= 7 &&
                    !checkForDevilExistence(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY()))) {
                return validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY()));
            }
            return card.getCoordinate();
        }
        //agar flag daste dusteshe ya dste doshmane
        Card targetCrd = null;
        for (int i = 0; i < fieldCards[1].length; i++) {
            if (checkCardEquality(fieldCards[1][i], mainFlag.getFlagHolder())) {
                targetCrd = fieldCards[1][i];
            }
        }
        for (int j = 0; j < fieldCards[0].length; j++) {
            if (checkCardEquality(fieldCards[0][j], mainFlag.getFlagHolder())) {
                targetCrd = fieldCards[0][j];
            }
        }
        switch (card.getAssaultType()) {
            case MELEE:
                assert targetCrd != null;
                if (Coordinate.getManhattanDistance(card.getCoordinate(), targetCrd.getCoordinate()) <= 4) {
                    Coordinate coordinate = makeNewCoordinate((card.getCoordinate().getX() + targetCrd.getCoordinate().getX()) / 2,
                            (card.getCoordinate().getY() + targetCrd.getCoordinate().getY()) / 2);
                    if (checkForDevilExistence(coordinate)) {
                        return coordinate;
                    }
                }
                return card.getCoordinate();
            case HYBRID:
            case RANGED:
                assert targetCrd != null;
                if (Coordinate.getManhattanDistance(card.getCoordinate(), targetCrd.getCoordinate()) <= 2 + card.getMaxRange()) {
                    switch (checkFourQuartersOfGround(card.getCoordinate(), targetCrd.getCoordinate())) {
                        case 3:
                            if (validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() - 1)) != null &&
                                    (validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY())) != null
                                            || validateMovement(makeNewCoordinate(card.getCoordinate().getX(), card.getCoordinate().getY() - 1)) != null)) {
                                return makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() - 1);
                            }
                        case 2:
                            if (validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() + 1)) != null &&
                                    (validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY())) != null
                                            || validateMovement(makeNewCoordinate(card.getCoordinate().getX(), card.getCoordinate().getY() + 1)) != null)) {
                                return makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() + 1);
                            }
                        case 1:
                            if (validateMovement(makeNewCoordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY() + 1)) != null &&
                                    (validateMovement(makeNewCoordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY())) != null
                                            || validateMovement(makeNewCoordinate(card.getCoordinate().getX(), card.getCoordinate().getY() + 1)) != null)) {
                                return makeNewCoordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY() + 1);
                            }
                        case 4:
                            if (validateMovement(makeNewCoordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY() - 1)) != null &&
                                    (validateMovement(makeNewCoordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY())) != null
                                            || validateMovement(makeNewCoordinate(card.getCoordinate().getX(), card.getCoordinate().getY() - 1)) != null)) {
                                return makeNewCoordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY() - 1);
                            }
                    }
                }
                return card.getCoordinate();
        }
        return card.getCoordinate();
    }

    //collectFlag
    private Coordinate setDestinationCoordinationModeThree(Card card) {
        int leastDistance = 15;
        int leastDistanceIndex = 0;
        for (int i = 0; i < flagsOnTheGround.size(); i++) {
            int distance = Coordinate.getManhattanDistance(card.getCoordinate(), flagsOnTheGround.get(i).getCoordinate());
            if (distance < leastDistance && flagsOnTheGround.get(i).getFlagHolder() == null) {
                leastDistance = distance;
                leastDistanceIndex = i;
            }
        }
        if (leastDistance <= 2) {
            return getCoordinate(card.getCoordinate(), flagsOnTheGround.get(leastDistanceIndex).getCoordinate());
        } else {
            switch (checkFourQuartersOfGround(card.getCoordinate(), flagsOnTheGround.get(leastDistanceIndex).getCoordinate())) {
                case 1:
                    return validateMovement(makeNewCoordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY() + 1));
                case 2:
                    return validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() + 1));
                case 3:
                    return validateMovement(makeNewCoordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() - 1));
                case 4:
                    return validateMovement(makeNewCoordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY() - 1));
            }

        }
        return card.getCoordinate();
    }

    private Coordinate getCoordinate(Coordinate c1, Coordinate c2) {
        if ((c1.getX() + 1 == c2.getX() && c1.getY() == c2.getY()) || (c1.getX() - 1 == c2.getX() && c1.getY() == c2.getY())
                || (c1.getX() == c2.getX() && c1.getY() + 1 == c2.getY()) || (c1.getX() == c2.getX() && c1.getY() - 1 == c2.getY())) {
            return validateMovement(c2);
        }
        if (c1.getX() == c2.getX() && c1.getY() + 2 == c2.getY()) {
            if (validateMovement(makeNewCoordinate(c1.getX(), c1.getY() + 1)) != null) {
                return validateMovement(c2);
            }
        }
        if (c1.getX() == c2.getX() && c1.getY() - 2 == c2.getY()) {
            if (validateMovement(makeNewCoordinate(c1.getX(), c1.getY() - 1)) != null) {
                return validateMovement(c2);
            }
        }
        if (c1.getX() - 2 == c2.getX() && c1.getY() == c2.getY()) {
            if (validateMovement(makeNewCoordinate(c1.getX() - 1, c1.getY())) != null) {
                return validateMovement(c2);
            }
        }
        if (c1.getX() + 2 == c2.getX() && c1.getY() == c2.getY()) {
            if (validateMovement(makeNewCoordinate(c1.getX() + 1, c1.getY())) != null) {
                return validateMovement(c2);
            }
        }
        if (c1.getX() + 1 == c2.getX() && c1.getY() + 1 == c2.getY()) {
            if ((validateMovement(makeNewCoordinate(c1.getX(), c1.getY() + 1)) != null)
                    || (validateMovement(makeNewCoordinate(c1.getX() + 1, c1.getY())) != null)) {
                return validateMovement(c2);
            }
        }
        if (c1.getX() - 1 == c2.getX() && c1.getY() + 1 == c2.getY()) {
            if ((validateMovement(makeNewCoordinate(c1.getX(), c1.getY() + 1)) != null)
                    || (validateMovement(makeNewCoordinate(c1.getX() - 1, c1.getY())) != null)) {
                return validateMovement(c2);
            }
        }
        if (c1.getX() - 1 == c2.getX() && c1.getY() - 1 == c2.getY()) {
            if ((validateMovement(makeNewCoordinate(c1.getX(), c1.getY() - 1)) != null)
                    || (validateMovement(makeNewCoordinate(c1.getX() - 1, c1.getY())) != null)) {
                return validateMovement(c2);
            }
        }
        if (c1.getX() + 1 == c2.getX() && c1.getY() - 1 == c2.getY()) {
            if ((validateMovement(makeNewCoordinate(c1.getX(), c1.getY() - 1)) != null)
                    || (validateMovement(makeNewCoordinate(c1.getX() + 1, c1.getY())) != null)) {
                return validateMovement(c2);
            }
        }
        return null;
    }

    private Coordinate setDestinationCoordinatesModeOne(Card card) {
        if (card.getType().equals("Minion")) {
            switch (card.getAssaultType()) {
                case MELEE:
                    if (card.isAbleToAttack()) {
                        boolean enemyIsNear = false;
                        for (int k = -1; k < 2; k++) {
                            for (int j = -1; j < 2; j++) {
                                for (int i = 0; i < getFieldCards()[0].length; i++) {
                                    if (getFieldCards()[0][i].getCoordinate().equals(new Coordinate(card.getCoordinate().getX() + k, card.getCoordinate().getY() + j))) {
                                        if (getFieldCards()[0][i].getType().equals("Hero")) {
                                            return card.getCoordinate();
                                        }
                                        enemyIsNear = true;
                                    }
                                }
                            }
                        }
                        Coordinate i = checkHeroDistance(card);
                        if (i != null) return i;
                        if (enemyIsNear) return card.getCoordinate();
                        return new Coordinate(card.getCoordinate().getX(), card.getCoordinate().getY());
                    }
                    break;
                case RANGED:
                case HYBRID:
                    if (checkForHero(card)) return card.getCoordinate();
                    for (int i = 0; i < fieldCards[0].length; i++) {
                        if (fieldCards[0][i].getType().equals("Hero") && Coordinate.getManhattanDistance(fieldCards[0][i].getCoordinate(), card.getCoordinate()) <= card.getMaxRange() + 2) {
                            if (checkFourQuartersOfGround(fieldCards[0][i].getCoordinate(), card.getCoordinate()) == 1) {
                                return validateMovement(new Coordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() - 1));
                            }
                            if (checkFourQuartersOfGround(fieldCards[0][i].getCoordinate(), card.getCoordinate()) == 2) {
                                return validateMovement(new Coordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY() - 1));
                            }
                            if (checkFourQuartersOfGround(fieldCards[0][i].getCoordinate(), card.getCoordinate()) == 3) {
                                return validateMovement(new Coordinate(card.getCoordinate().getX() - 1, card.getCoordinate().getY() + 1));
                            }
                            if (checkFourQuartersOfGround(fieldCards[0][i].getCoordinate(), card.getCoordinate()) == 4) {
                                return validateMovement(new Coordinate(card.getCoordinate().getX() + 1, card.getCoordinate().getY() + 1));
                            }
                        }
                    }
                    ArrayList<Card> cards = new ArrayList<>();
                    for (int i = 0; i < fieldCards.length; i++) {
                        if (Coordinate.getManhattanDistance(fieldCards[0][i].getCoordinate(), card.getCoordinate()) < card.getMaxRange()) {
                            cards.add(fieldCards[0][i]);
                        }
                    }
                    if (cards.size() != 0) return cards.get(0).getCoordinate();
                    return card.getCoordinate();
            }
            if (!card.isAbleToMove()) {
                return card.getCoordinate();
            }
        } else if (card.getType().equals("Hero")) {
            return validateMovement(new Coordinate(card.getCoordinate().getX(), card.getCoordinate().getY() - 1));
        }
        return new Coordinate(card.getCoordinate().getX(), card.getCoordinate().getY());
    }

    private Coordinate checkHeroDistance(Card card) {
        for (int i = 0; i < getFieldCards()[0].length; i++) {
            if (getFieldCards()[0][i].getType().equals("Hero")) {
                if (Coordinate.getManhattanDistance(card.getCoordinate(), getFieldCards()[0][i].getCoordinate()) < 4) {
                    return new Coordinate((card.getCoordinate().getX() + getFieldCards()[0][i].getCoordinate().getX()) / 2,
                            (card.getCoordinate().getY() + getFieldCards()[0][i].getCoordinate().getY()) / 2);
                }
            }
        }
        return null;
    }

    private Coordinate validateMovement(Coordinate coordinate) {

        if (field[coordinate.getX()][coordinate.getY()].getCardID() == 0) {
            return coordinate;
        }
        return null;
    }

    private boolean checkForHero(Card card) {
        for (int i = 0; i < fieldCards[0].length; i++) {
            if (fieldCards[0][i].getType().equals("Hero") && Coordinate.getManhattanDistance(fieldCards[0][i].getCoordinate(), card.getCoordinate()) <= card.getMaxRange()) {
                return true;
            }
        }
        return false;
    }

    private int checkFourQuartersOfGround(Coordinate c1, Coordinate c2) {
        if (c1.getX() >= c2.getX() && c1.getY() >= c2.getY()) {
            return 4;
        }
        if (c1.getX() <= c2.getX() && c1.getY() >= c2.getY()) {
            return 3;
        }
        if (c1.getX() >= c2.getX() && c1.getY() <= c2.getY()) {
            return 1;
        }
        if (c1.getX() <= c2.getX() && c1.getY() <= c2.getY()) {
            return 2;
        }
        return 0;
    }

    public Card chooseCard(ArrayList<Card> cards) {

        int[] bestCardToChoose = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getBuffs().size() == 1) {
                bestCardToChoose[i] = 10;
                chooseBestCard(cards, bestCardToChoose, i, 0);

            }
            if (cards.get(i).getBuffs().size() == 2) {
                bestCardToChoose[i] = 100;
                chooseBestCard(cards, bestCardToChoose, i, 0);
                chooseBestCard(cards, bestCardToChoose, i, 1);
            }
        }
        int highestAP = 0;
        int whichCard = 0;
        for (int i = 0; i < bestCardToChoose.length; i++) {
            if (bestCardToChoose[i] > highestAP) {
                highestAP = bestCardToChoose[i];
                whichCard = i;
            }
        }
        Card card = cards.get(whichCard);
        cards.remove(whichCard);
        return card;
    }

    private void chooseBestCard(ArrayList<Card> cards, int[] bestCardToChoose, int i, int whichBuff) {
        if (cards.get(i).getBuffs().get(whichBuff).getActivationType().equals(ActivationType.ON_ATTACK)) {
            bestCardToChoose[i] += 5;
        }
        if (cards.get(i).getBuffs().get(whichBuff).getActivationType().equals(ActivationType.ON_DEATH)) {
            bestCardToChoose[i] += 4;
        }
        if (cards.get(i).getBuffs().get(whichBuff).getActivationType().equals(ActivationType.ON_DEFENCE)) {
            bestCardToChoose[i] += 3;
        }
        if (cards.get(i).getBuffs().get(whichBuff).getActivationType().equals(ActivationType.PASSIVE)) {
            bestCardToChoose[i] += 2;
        }
        if (cards.get(i).getBuffs().get(whichBuff).getActivationType().equals(ActivationType.COMBO)) {
            bestCardToChoose[i] += 1;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int getTurnByAccount(Account account) {
        if (this.accounts[0].getId() == account.getId())
            return 0;
        return 1;
    }

    public void useItem(Item item) {
        for (ItemBuff buff : item.getBuffs()) {
            if (buff.getTargetCard().equals("Account")) {
                applyItem(buff, targetCard);
            } else {
                if (targetCard.isClass(buff.getTargetCard())) {
                    if (buff.getSide() == Side.ENEMY &&
                            Arrays.asList(fieldCards[turn % 2]).indexOf(currentCard) != -1)
                        continue;
                    if (buff.getSide() == Side.COMRADE &&
                            Arrays.asList(fieldCards[(turn + 1) % 2]).indexOf(currentCard) != -1)
                        continue;
                    switch (buff.getCasterActivationType()) {
                        case ON_ATTACK:
                            if (currentCard.isClass(buff.getCasterCard()) && attackMode) {
                                applyItem(buff, targetCard);
                            }
                            break;
                        case ON_SPAWN:
                            if (isOnSpawn) {
                                applyItem(buff, targetCard);
                            }
                            break;
                        case ON_DEFENCE:
                            if (currentCard.isClass(buff.getCasterCard()) && !attackMode) {
                                applyItem(buff, targetCard);
                            }
                        case ON_DEATH:
                            if (currentCard.isClass(buff.getCasterCard()) && currentCard.getHealthPoint() <= 0) {
                                applyItem(buff, targetCard);
                            }
                            break;
                        case UNDEFINED:
                            applyItem(buff, targetCard);
                            break;
                    }
                }
            }
        }
    }

    public void applyItem(ItemBuff buff, Card card) {
        switch (buff.getType()) {
            case POISON:
                card.modifyHealth(buff.getPower());
            case STUN:
                card.setAbleToAttack(false);
                System.out.println("1812");
                card.setAbleToMove(false);
                break;
            case DISARM:
                card.setAbleToCounter(false);
                break;
            case HIT_POWER:
                card.modifyHit(buff.getPower());
                break;
            case HEALTH_POWER:
                card.modifyHealth(buff.getPower());
                break;
            case HIT_WEAKNESS:
                card.modifyHit(buff.getPower());
                break;
            case HEALTH_WEAKNESS:
                card.modifyHealth(buff.getPower());
                break;
            case MANA:
                accounts[turn % 2].modifyMana(buff.getPower());
                return;
        }
        card.getCastedItems().add(buff);
    }


    public void endGame() {
        if (!checkForWin()) {
            resign();
        }
    }

    private void randomizeDeck(int current) {
        ArrayList<Card> random = new ArrayList<>();
        Deck deck = accounts[current].getCollection().getMainDeck();
        int r;
        for (int i = Constants.MAXIMUM_DECK_SIZE; i > 0; i--) {
            r = rand.nextInt(i);
            random.add(deck.getCards().get(r));
            deck.getCards().remove(r);
        }
        accounts[current].getCollection().getMainDeck().setCards(random);


    }

    private void addToHand(int current) {
        Deck deck = accounts[current].getCollection().getMainDeck();
        int last = Constants.MAXIMUM_HAND_SIZE;
        for (int i = 0; i < Constants.MAXIMUM_HAND_SIZE; i++) {
            if (playerHands[current][i] == null) {
                last = i;
                break;
            }
        }
        for (int i = last; i < Constants.MAXIMUM_HAND_SIZE; i++) {
            playerHands[current][i] = deck.getCards().get(0);
            deck.getCards().remove(0);
        }

    }

    private void refactorDeck(int current) {
        for (int i = 0; i < playerHands[current].length; i++) {
            accounts[current].getCollection().getCards().add(playerHands[current][i]);

        }
        for (int i = 0; i < graveyard[current].length; i++) {
            accounts[current].getCollection().getCards().add(graveyard[current][i]);

        }
        for (Card card :
                fieldCards[current]) {
            accounts[current].getCollection().getCards().add(card);
        }

    }

    private void initializeHands() {
        randomizeDeck(0);
        randomizeDeck(1);
        playerHands[0] = accounts[0].getCollection().getMainDeck().getCards()
                .subList(0, Constants.MAXIMUM_HAND_SIZE).toArray(new Card[Constants.MAXIMUM_HAND_SIZE]);
        playerHands[1] = accounts[1].getCollection().getMainDeck().getCards()
                .subList(0, Constants.MAXIMUM_HAND_SIZE).toArray(new Card[Constants.MAXIMUM_HAND_SIZE]);
        for (int i = 0; i < Constants.MAXIMUM_HAND_SIZE; i++) {
            accounts[0].getCollection().getMainDeck().getCards().remove(0);
            accounts[1].getCollection().getMainDeck().getCards().remove(0);
        }
    }
}
