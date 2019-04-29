package Controller;

import Model.*;
import View.*;

public class Controller {
    private View view = View.getInstance();
    private Game game = Game.getInstance();
    private Menu menu = Menu.getInstance();
    private Account account = new Account();
    private static final Controller controller = new Controller();

    private Controller() {

    }

    public static Controller getInstance() {
        return controller;
    }

    public void main() {
        Request request = new Request();
        request.getNewCommand();
        switch (request.getType()) {
            case CREATE_ACCOUNT:
                createAccount(request);
                break;
            case LOGIN:
                login(request);
                break;
            case LEADERBOARD:
                showLeaderBoard(request);
                break;
            case SAVE:
                save();
                break;
            case LOGOUT:
                logout(request);
                break;
            case HELP:
                help();
                break;
            case EXIT:
                exit();
                break;
            case SHOW_COLLECTION:
                showTheCollection();
                break;
            case SEARCH_COLLECTION:
                searchInCollection(request);
                break;
            case SAVE_IN_COLLECTION :
                saveCollection();
                break;
            case CREATE_DECK:
                createDeck(request);
                break;
            case DELETE_DECK:
                deleteDeck(request);
                break;
            case ADD:
                addToDeck(request);
                break;
            case REMOVE:
                removeFromDeck(request);
                break;
            case VALIDATION:
                validateDeck(request);
                break;
            case SELECT_DECK:
                selectDeck(request);
        }
    }

    public void createAccount(Request request) {
        if (request.checkAccountCreationSyntax()) {
            String username = request.getCommand();
            view.passwordInsertion();
            String password = request.getNewCommand();
            Account account = new Account(request.getAccountName(username), request.getPassword(password));
            view.accountCreation(account.createAccount());
        }
    }

    public void login(Request request) {
        if (request.checkLoginSyntax()) {
            String username = request.getCommand();
            view.passwordInsertion();
            String password = request.getNewCommand();
            view.login(Account.login(request.getAccountName(username), request.getPassword(password)));
        }
    }

    public void showLeaderBoard(Request request) {
        if (request.checkLeaderBoardSyntax()) {
            view.printLeaderboard();
        }
    }

    public void save() {

    }

    public void logout(Request request) {
        if (request.checkLogoutSyntax()) {
            view.logout();
        }
    }

    public void help() {
        view.printHelp();
    }

    public void enter(Request request) {

    }

    public void exit() {
        menu.exitMenu();
    }

    public void showTheCollection() {
        view.printCollection(this.account.getCollection());
    }


    public void searchInCollection(Request request) {
        if (request.checkSearchSyntax()) {
            view.printId(Card.getAllCardsId(request.getCardName(request.getCommand()),
                    this.account.getCollection().getCards().toArray(Card[]::new)).toArray(Card[]::new));
            view.printId(Item.getAllItemsId(request.getCardName(request.getCommand()),
                    this.account.getCollection().getItems().toArray(Item[]::new)).toArray(Item[]::new));
        }
    }

    public void saveCollection() {

    }

    public void createDeck(Request request) {
        if(request.checkDeckSyntax()){
            view.createDeck(this.account.getCollection().createDeck(request.getDeckName(request.getCommand())));
        }
    }

    public void deleteDeck(Request request) {
        if(request.checkDeckSyntax()){
            view.deleteDeck(this.account.getCollection().deleteDeck(request.getDeckName(request.getCommand())));
        }
    }

    public void addToDeck(Request request) {
        if(request.checkToDeckAdditionSyntax()){
            view.addToCollection(this.account.getCollection().add(request.getDeckName(request.getCommand()),request.getObjectID(request.getCommand())));
        }
    }

    public void removeFromDeck(Request request) {
        if(request.checkFromDeckDeletionSyntax()){
            view.removeFromDeck(this.account.getCollection().remove(request.getDeckName(request.getCommand()),request.getObjectID(request.getCommand())));
        }
    }

    public void validateDeck(Request request) {
        if(request.checkValidationSyntax()){
            view.checkValidation(this.account.getCollection().validate(request.getDeckName(request.getCommand())));
        }
    }

    public void selectDeck(Request request) {
        if(request.checkDeckSelectionSyntax()){
            view.printDeckSelection(this.account.getCollection().selectDeck(request.getDeckName(request.getCommand())));
        }
    }

    public void showAllDecks() {

    }

    public void showDeck() {

    }

    public void helpCollection() {

    }

    public void exitShop() {

    }

    public void showCollectionShop() {

    }

    public void searchInShop() {

    }

    public void buy() {

    }

    public void sell() {

    }

    public void showShop() {

    }

    public void helpShop() {

    }

    public void gameInfo() {

    }

    public void showMyMinions() {

    }

    public void showOppMinions() {

    }

    public void selectCardInBattle() {

    }

    public void moveToInBattle() {

    }

    public void battleAttack() {

    }

    public void battleComboAttack() {

    }

    public void useSpecialPower() {

    }

    public void showHand() {

    }

    public void insertCard() {

    }

    public void endTurn() {

    }

    public void showCollectables() {

    }

    public void selectCollectables() {

    }

    public void showBattleInfo() {

    }

    public void useItem() {

    }

    public void showNextCard() {

    }

    public void enterTheGraveyard() {

    }

    public void showCardInfo() {

    }

    public void showCards() {

    }

    public void battleHelp() {

    }

    public void endGame() {

    }

    public void showMenu() {

    }
}
