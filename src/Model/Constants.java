package Model;

public class Constants {
    public static final String BUFF_INFO_SPLITTER = " ; ";
    public static final String CARD_INFO_SPLITTER = " , ";
    public static final int UNDEFINED_HP = -1;
    public static final int UNDIFINED_MANA = -1;
    public static final int UNDIFINED_AP = -1;
    public static final AssaultType UNDEFINED_assaultType = AssaultType.NULL;
    public static final int UNDEFINED_MAX_MOVE = -1;
    public static final int UNDEFINED_MIN_RANGE = -1;
    public static final int UNDEFINED_MAX_RANGE = -1;
    public static final ActivationType UNDEFINED_ACTIVATION_TYPE = ActivationType.UNDEFINED;
    public static final int ALL_FIELD = -1;
    public static final int COLUMN = -2;
    public static final int ROW = -3;
    public static final int ALL_TIME = 1000000;
    public static final int UNDIFINED_POWER = 0;
    public static final Side ALL_SIDES = Side.ALL;
    public static final int MAX_MANA = 9;
    public static final int LENGTH = 9;
    public static final int WIDTH = 5;
    public static final int ITEM_APPEARANCE = 3;
    public static final int MAXIMUM_FLAGS = 6;
    public static final int TURNS_HOLDING_FLAG = 6;
    public static final int randomXGenerator = 2;
    public static final int randomYGenerator = 4;
    public static final int shiftColumn = 6;
    public static final int initialBudget = 15000;
    public static final int spellId = 20000;
    public static final int minionId = 30000;
    public static final int heroId = 40000;
    public static final int itemId = 50000;
    public static final int accoutnId = 10000;
    public static final int MAXIMUM_DECK_SIZE = 20;
    public static final int MAXIMUM_HAND_SIZE = 5;
    public static final int INITIAL_MANA = 2;
    public static final double WINDOW_WIDTH = 1300;
    public static final double WINDOW_HEIGHT = 700;
    public static final double BUTTON_WIDTH = 80;
    public static final double BUTTON_HEIGHT = 20;
    public static final double CENTRE_X = WINDOW_WIDTH / 2.0;
    public static final double CENTRE_Y = WINDOW_HEIGHT / 2.0;
    public static final String TEXT_FONT = "Chilanka";
    public static final double FIELD_WIDTH = 120;
    public static final double FIELD_HEIGHT = 30;
    public static final double SINGLE_PLAYER_WIDTH = 300;
    public static final double SINGLE_PLAYER_HEIGHT = 300;
    public static int HEROES_COUNT = 10;
    public static int MINIONS_COUNT = 40;
    public static int SPELLS_COUNT = 20;
    public static int ITEMS_COUNT = 20;
    public static final double ARROW = 50;
    public static final double SLIDE = 100;
    public static final double CARD_WIDTH = 160;
    public static final double CARD_HEIGHT = 11.0 / 8.0 * CARD_WIDTH;
    public static final double GIF_WIDTH = 7.0 / 8.0 * CARD_WIDTH;
    public static final double CARD_X = 400;
    public static final double CARD_Y = 70;
    public static final double CARD_X_GAP = 100;
    public static final double CARD_Y_GAP = 20;
    public static final double GIF_HEIGHT = GIF_WIDTH;
    public static final double CARD_POWER_X = 3.6 / 16.0 * CARD_WIDTH;
    public static final double CARD_HEALTH_X = CARD_POWER_X * 3.4;
    public static final double CARD_INFO_Y = 6.0 / 11.0 * CARD_HEIGHT;
    public static final double CARD_TYPE_Y = 10.0;
    public static final double CARD_TYPE_X = CARD_POWER_X * 2.2;
    public static final double CARD_NAME_Y = CARD_HEIGHT - 4 * CARD_TYPE_Y;
    public static final double CARD_NAME_X = CARD_TYPE_X;
    public static final double CARD_PRICE_X = CARD_NAME_X;
    public static final double CARD_PRICE_Y = CARD_NAME_Y * 0.9;
    public static final int FONT_SIZE = 20;
    public static final double CARD_INFO_FONT = FONT_SIZE * 0.55;
    public static final String PAGE_TITLE_FONT = "Chilanka";
    public static final double PAGE_TITLE_SIZE = 2 * FONT_SIZE;
    public static final double PAGE_TITLE_Y = 10;
    public static final int CARD_PER_ROW = 3;
    public static final int CARD_PER_COLUMN = 2;
    public static final int CARD_PER_PAGE = CARD_PER_ROW * CARD_PER_COLUMN;
    public static final double CURSOR_LENGTH = 16;
    public static final double IMAGE_BUTTON_REL_X = 23;
    public static final double IMAGE_BUTTON_REL_Y = 3;
    public static final double SCROLLER_X = CARD_X + 1.5 * CARD_WIDTH + CARD_X_GAP;
    public static final double SCROLLER_Y = WINDOW_HEIGHT - 100;
    public static final String INFO_FONT = "Ubuntu";
    public static final double SELL_WIDTH = 150;
    public static final double SELL_HEIGHT = 50;
    public static final int SELL_TEXT_SIZE = 30;
    public static final double SELECTION_WIDTH = 70;
    public static final double SELECTION_HEIGHT = 20;
    public static final double SELL_PANE_X = 50;
    public static final double SELL_X = 50;
    public static final double SELL_Y = 10;
    public static final double DECK_PANE_WIDTH = SELL_WIDTH * 1.2;
    public static final double DECK_PANE_HEIGHT = SELL_HEIGHT * 0.7;
    public static final double DECK_PANE_X = SELL_PANE_X;
}
