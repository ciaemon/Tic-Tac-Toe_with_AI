package tictactoe;

import javafx.scene.control.Cell;
import javafx.util.Pair;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Pattern;

enum CellState {
    EMPTY (' '),
    X ('X'),
    O('O');
    char alias;

    CellState(char alias) {
        this.alias = alias;
    }

    static CellState getState(char ch) {
        switch (ch) {
            case 'X' :
            case 'x' :
                return CellState.X;
            case 'O' :
            case 'o' :
                return CellState.O;
            default : return CellState.EMPTY;
        }
    }
}

enum PlayFieldStatus {
    GAME_NOT_FINISHED ("Game not finished"),
    X_WIN ("X wins"),
    O_WIN ("O wins"),
    DRAW ("Draw"),
    IMPOSSIBLE ("Impossible");
    String message;

    PlayFieldStatus(String message) {
        this.message = message;
    }
}
class Move {
    int x;
    int y;
}
class PlayField {

    /**
     * Stores play field in two-dimensional array of CellState.
     */
    CellState[][] field = new CellState[3][3];



    public PlayField(CellState[][] field) {
        this.field = field;
    }

    /**
     * Creates new PlayField with all empty cells;
     */
    PlayField() {
       this.field = new CellState[][]{
               {CellState.EMPTY, CellState.EMPTY, CellState.EMPTY},
               {CellState.EMPTY, CellState.EMPTY, CellState.EMPTY},
               {CellState.EMPTY, CellState.EMPTY, CellState.EMPTY}
       };
    }

    /**
     * @param input - String parameter. It must contains exactly 9 characters (X, O, _ - case-insensitive),
     *              field will have following pattern:
     *              0 1 2
     *              3 4 5
     *              6 7 8
     *              otherwise all cells are empty.
     */
    PlayField(String input) {
        if (!input.matches("[xXoO_]{9}")) {
            new PlayField();
        }
        for (int i = 0; i < 9; i++) {
            this.field[i % 3][2 - i / 3] = CellState.getState(input.charAt(i));
        }
    }

    /**
     * Prints play field. Field cells have following pattern:
     * ---------
     * (1, 3) (2, 3) (3, 3)
     * (1, 2) (2, 2) (3, 2)
     * (1, 1) (2, 1) (3, 1)
     * ---------
     * where (i, j) alias of CellState with coordinates i, j
     */
    public void draw() {
        System.out.println("---------");
        System.out.printf("| %s %s %s |\n", field[0][2].alias, field[1][2].alias, field[2][2].alias);
        System.out.printf("| %s %s %s |\n", field[0][1].alias, field[1][1].alias, field[2][1].alias);
        System.out.printf("| %s %s %s |\n", field[0][0].alias, field[1][0].alias, field[2][0].alias);
        System.out.println("---------");
    }

    /**
     * @return String representation of field (as input).
     */
    public String toString() {
        return "" + field[0][2].alias + field[1][2].alias + field[2][2].alias +
                    field[0][1].alias + field[1][1].alias + field[2][1].alias +
                    field[0][0].alias + field[1][0].alias + field[2][0].alias;
    }

    /**
     * @return String representation of field status. There are five possible results:
     * "Impossible", "X wins", "O wins", "Draw", "Game not finished"
     */
    public PlayFieldStatus check() {

        Pair<Integer, Integer> xo = countXO();
        if (xo.getKey() - xo.getValue() > 1 || xo.getKey() - xo.getValue() < 0) return PlayFieldStatus.IMPOSSIBLE;
        boolean XWin = false; // field contains X winning lines
        boolean OWin = false; // field contains O winning lines
        boolean emptyCells = !(xo.getKey() + xo.getValue() == 9); // field contains empty cells
        /* Determination of XWin and OWin */
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) || //checking i-th row and
                field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i])) { //column
               /* field[i][i] contains winning side */
                XWin = XWin || field[i][i].equals(CellState.X);
                OWin = OWin || field[i][i].equals(CellState.O);
            }
        }

        if (field[0][0].equals(field[1][1]) && field[2][2].equals(field[1][1]) || //checking diagonals
            field[0][2].equals(field[1][1]) && field[2][0].equals(field[1][1])) {
            /* Central cell contains winning side */
            XWin = XWin || field[1][1].equals(CellState.X);
            OWin = OWin || field[1][1].equals(CellState.O);
        }
        /* Result determination using XWin, OWin and emptyCells  */
        if (XWin && OWin) {
            return PlayFieldStatus.IMPOSSIBLE;
        }
        if (XWin) {
            return PlayFieldStatus.X_WIN;
        }
        if (OWin) {
            return PlayFieldStatus.O_WIN;
        }
        return emptyCells ?  PlayFieldStatus.GAME_NOT_FINISHED : PlayFieldStatus.DRAW;
    }

    /**
     * @return true if now X's turn, otherwise - false.
     */
    public boolean isXTurn() {
        return countXO().getKey().equals(countXO().getValue());
    }

    /**
     * @return Pair of X and O quantities on the field
     */
    private Pair<Integer, Integer> countXO() {
        int x = 0;
        int o = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j].equals(CellState.X)) {
                    x++;
                } else if (field[i][j].equals(CellState.O)) {
                    o++;
                }
            }
        }
        return new Pair<>(x, o);
    }

    /**
     * @param x column number from left
     * @param y row number from bottom
     * @return CellState of cell
     */
    public CellState getCell(int x, int y) {
        if (x > 3 || x < 1 || y > 3 || y < 1) {
            return null;
        }
        return field[x - 1][y - 1];
    }

    /**
     * Setting cell
     * @param x column number from left
     * @param y row number from bottom
     * @param state CellState to set
     * @return true if success
     */
    public boolean setCell(int x, int y, CellState state) {
        if (x > 3 || x < 1 || y > 3 || y < 1) { return false; }
        field[x - 1][y - 1] = state;
        return true;
    }

    /**
     * Making a move
     * @param x column number from left
     * @param y row number from bottom
     * @return code of result. 1 - success, 0 - cell is occupied, -1 - x, y are out of range
     */
    public int makeAMove(int x, int y) {
        if (x > 3 || x < 1 || y > 3 || y < 1) { return -1; }
        if (!(field[x - 1][y - 1].equals(CellState.EMPTY))) { return 0; }
        return setCell(x, y, isXTurn() ? CellState.X : CellState.O) ? 1 : -1;
    }

    public Pair<Integer, Integer> findWin(boolean currentPlayer) {
        CellState current = isXTurn() ^ currentPlayer ? CellState.O : CellState.X;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++ ) {
                if (field[i][j].equals(CellState.EMPTY)) {
                    field[i][j] = current;
                    if ((field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2])) ||
                        (field[0][j].equals(field[1][j]) && field[0][j].equals(field[2][j])) ||
                        (field[1][1].equals(field[0][0]) && field[2][2].equals(field[0][0])) ||
                        (field[1][1].equals(field[2][0]) && field[0][2].equals(field[0][0]))) {
                        field[i][j] = CellState.EMPTY;
                        return new Pair<>(++i, ++j);
                    } else {
                        field[i][j] = CellState.EMPTY;
                    }
                }
            }
        }
        return null;
    }


}




abstract class Player {
    protected String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract Pair<Integer, Integer> move(PlayField field);
    public CellState side(PlayField field) {
        return field.isXTurn() ? CellState.X : CellState.O;
    }
}

class Human extends Player {

    public Human(String name) {
        super(name);
    }

    @Override
    Pair<Integer, Integer> move(PlayField field) {

        Integer x;
        Integer y;
        boolean error = true;
        do {
            System.out.print("Enter the coordinates: ");
            x = Main.scanner.hasNextInt() ? Main.scanner.nextInt() : null;
            y = Main.scanner.hasNextInt() ? Main.scanner.nextInt() : null;
            Main.scanner.nextLine();
            if (x.equals(null) || y.equals(null)) {
                System.out.println("You should enter numbers!");
                continue;
            }
            switch (field.makeAMove(x, y)) {
                case -1 :
                    System.out.println("Coordinates should be from 1 to 3!");
                    break;
                case 0 :
                    System.out.println("This cell is occupied! Choose another one!");
                    break;
                case 1 :
                    error = false;
                    break;
            }
        } while (error);
        return new Pair<>(x, y);
    }

}

abstract class TicTacToeAI extends Player {
    protected int level;
    protected String levelName;

    public TicTacToeAI(String name) {
        super(name);
    }

    protected void printMessage() {
        System.out.printf("Making move level \"%s\"\n", levelName);
    }



    public int getLevel() {
        return level;
    }

    public String getLevelName() {
        return levelName;
    }
}

class TicTacToeAIEasy extends TicTacToeAI {
    private final Random random = new Random();

    public TicTacToeAIEasy(String name) {
        super(name);
        level = 0;
        levelName = "easy";
    }

    @Override
    Pair<Integer, Integer> move(PlayField field) {
        int x, y;
        do {
            x = random.nextInt(3) + 1;
            y = random.nextInt(3) + 1;
        } while (!field.getCell(x, y).equals(CellState.EMPTY));
        printMessage();
        return new Pair<>(x, y);
    }
}

class TicTacToeAIMedium extends TicTacToeAI {
    private final Random random = new Random();

    public TicTacToeAIMedium(String name) {
        super(name);
        level = 1;
        levelName = "medium";
    }

    @Override
    Pair<Integer, Integer> move(PlayField field) {

       Pair<Integer, Integer> win  = field.findWin(true);
       Pair<Integer, Integer> lose = field.findWin(false);
        if (win != null) {
            printMessage();
            return win;
        }
        if (lose != null) {
            printMessage();
            return lose;
        }
        int x, y;
        do {
            x = random.nextInt(3) + 1;
            y = random.nextInt(3) + 1;
        } while (!field.getCell(x, y).equals(CellState.EMPTY));
        printMessage();
        return new Pair<>(x, y);
    }
}

class TicTacToeAIHard extends TicTacToeAI {

    public TicTacToeAIHard(String name) {
        super(name);
        level = 2;
        levelName = "hard";
    }

    private LinkedList<> moveList(PlayField field) {

    }

    @Override
    Pair<Integer, Integer> move(PlayField field) {
        return null;
    }
}

class Game {
    private PlayField field;
    private Player playerX;
    private Player playerO;

    public Game(PlayField newField, Player newPlayerX, Player newPlayerO) {
        field = newField;
        playerX = newPlayerX;
        playerO = newPlayerO;
    }

    public void process() {
        PlayFieldStatus result;
        field.draw();
        do {
            Player currentPlayer = field.isXTurn() ? playerX : playerO;
            Pair<Integer, Integer> move = currentPlayer.move(field);
            field.makeAMove(move.getKey(), move.getValue());
            field.draw();
            result = field.check();
        } while (result.equals(PlayFieldStatus.GAME_NOT_FINISHED));

        System.out.println(result.message);

    }
}
class GameFactory {
    public static Game newGame(String playerX, String playerO) {
        return new Game(new PlayField(), createPlayer(playerX), createPlayer(playerO));
    }

    public static Player createPlayer(String player) {
        switch (player) {
            case "user" : return new Human("Default");
            case "easy" : return new TicTacToeAIEasy("Dummy");
            case "medium" : return new TicTacToeAIMedium("Smart");
            case "hard" : return new TicTacToeAIHard("Champion");
            default: return null;
        }
    }
}

public class Main {
    final static java.util.Scanner scanner = new java.util.Scanner(System.in);
    final static Pattern START = Pattern.compile("start(( user)|( easy)|( medium)|( hard)){2}");
    final static Pattern EXIT = Pattern.compile("exit");
    final static Pattern HELP = Pattern.compile("help");

    public static void main(String[] args) {
      String input;
      do {
          System.out.print("Input command: ");
          input = scanner.nextLine();
          if (START.matcher(input).matches()) {
              String[] command = input.split(" ");
              Game game = GameFactory.newGame(command[1], command[2]);
              game.process();
              continue;
          } else if (EXIT.matcher(input).matches()) {
              break;
          } else if (HELP.matcher(input).matches()) {
              System.out.println("Help message");
          } else {
              System.out.println("Bad parameters!");
              continue;
          }
      } while (true);
    }
}