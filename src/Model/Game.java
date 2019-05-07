package Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Game {
    private ArrayList<Account> accounts = new ArrayList<>();
    private ArrayList<Account> loggedInAccounts = new ArrayList<>();
    private static final Game SINGLETON_CLASS = new Game();
    private GameType gameType;
    private BattleMode mode;
    private Menu menu = Menu.getInstance();
    private Shop shop = Shop.getInstance();
    private int lastSpellId = Constants.spellId;
    private int lastMinionId = Constants.minionId;
    private int lastHeroId = Constants.heroId;
    private int lastItemId = Constants.itemId;
    private int lastAccountId = Constants.accoutnId;

    private Game() {

    }

    public int getLastSpellId() {
        return lastSpellId;
    }

    public void incrementSpellId() {
        this.lastSpellId++;
    }

    public int getLastMinionId() {
        return lastMinionId;
    }

    public void incrementMinionId() {
        this.lastMinionId++;
    }

    public int getLastHeroId() {
        return lastHeroId;
    }

    public void incrementHeroId() {
        this.lastHeroId++;
    }

    public int getLastItemId() {
        return lastItemId;
    }

    public void incrementItemId() {
        this.lastItemId++;
    }

    public int getLastAccountId() {
        return lastAccountId;
    }

    public void incrementAccountId() {
        this.lastAccountId++;
    }

    public void incrementSpellId(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public void setLoggedInAccounts(ArrayList<Account> loggedInAccounts) {
        this.loggedInAccounts = loggedInAccounts;
    }

    public static Game getSingletonClass() {
        return SINGLETON_CLASS;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public BattleMode getMode() {
        return mode;
    }

    public void setMode(BattleMode mode) {
        this.mode = mode;
    }

    private ArrayList<Account> getLoggedInAccounts() {
        for (Account account :
                accounts) {
            if (account.isLoggedIn()) {
                loggedInAccounts.add(account);
            }
        }
        return loggedInAccounts;
    }

    public void createBattle() {
        Battle battle = new Battle(loggedInAccounts.toArray(new Account[loggedInAccounts.size()]), gameType, mode);
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public static Game getInstance() {
        return SINGLETON_CLASS;
    }

    public boolean logout(Account account) {
        account.setLoggedIn(false);
        save(account);
        menu.setStat(MenuStat.MAIN);
        return true;
    }

    public void save(Account account) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(account);
        try {
            FileWriter writer = new FileWriter(account.getName() + ".json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sortAccounts() {
        Comparator<Account> compareById = Comparator.comparingInt(Account::getWins);
        accounts.sort(compareById.reversed());
    }

    public void initializeAccounts() throws Exception {
        File dir = new File("./");
        if (dir.exists()) {
            if (dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        if (!file.getName().matches("\\w+[.]json"))
                            continue;
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        Account account = new Gson().fromJson(reader, Account.class);
                        accounts.add(account);
                        accountObjectInitializer(account);
                    }
                }
            }
        }
    }

    private void accountObjectInitializer(Account account) {
        sortCards(account.getCollection().getCards());
        sortItems(account.getCollection().getItems());
        Collection collection = account.getCollection();
        for (int i = 0; i < collection.getCards().size(); i++) {
            if (collection.getCards().get(i).getType().equals("Spell"))
                if (lastSpellId < collection.getCards().get(i).getId())
                    lastSpellId = collection.getCards().get(i).getId();
            if (collection.getCards().get(i).getType().equals("Minion"))
                if (lastMinionId < collection.getCards().get(i).getId())
                    lastMinionId = collection.getCards().get(i).getId();
            if (collection.getCards().get(i).getType().equals("Hero"))
                if (lastHeroId < collection.getCards().get(i).getId())
                    lastHeroId = collection.getCards().get(i).getId();
        }
        try {
            lastItemId = account.getCollection().getItems().get(account.getCollection().getItems().size() - 1).getId();
        } catch (Exception e) {
        }
    }

    private void sortCards(ArrayList<Card> cards) {
        Comparator<Card> compareById = Comparator.comparingInt(Card::getId);
        cards.sort(compareById);
    }

    private void sortItems(ArrayList<Item> items) {
        Comparator<Item> compareById = Comparator.comparingInt(Item::getId);
        items.sort(compareById);
    }

    public void initializeHero() throws Exception {
        File dir = new File("./src/Objects/Cards/Heroes");
        if (dir.exists()) {
            if (dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        Hero hero = new Gson().fromJson(reader, Hero.class);
                        hero.setType("Hero");
                        hero.setId(++lastHeroId);
                        shop.getCards().add(hero);
                    }
                }
            }
        }
    }

    public void initializeMinion() throws Exception {
        File dir = new File("./src/Objects/Cards/Minions");
        if (dir.exists()) {
            if (dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        Minion minion = new Gson().fromJson(reader, Minion.class);
                        minion.setType("Minion");
                        minion.setId(++lastMinionId);
                        shop.getCards().add(minion);
                    }
                }
            }
        }
    }

    public void initializeSpell() throws Exception {
        File dir = new File("./src/Objects/Cards/Spells");
        if (dir.exists()) {
            if (dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        Spell spell = new Gson().fromJson(reader, Spell.class);
                        spell.setType("Spell");
                        spell.setId(++lastSpellId);
                        shop.getCards().add(spell);
                    }
                }
            }
        }
    }

    public void initializeItem() throws Exception {
        File dir = new File("./src/Objects/Items");
        if (dir.exists()) {
            if (dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        Item item = new Gson().fromJson(reader, Item.class);
                        item.setId(++lastItemId);
                        shop.getItems().add(item);
                    }
                }
            }
        }
    }

}
