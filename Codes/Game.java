import java.util.Scanner;

public class Game {
    // Portable newline
    private final static String NEWLINE = System.getProperty("line.separator");
    private Dungeon dungeon;     // The dungeon
    private char MONSTER;        // Name of the monster (A - Z)
    private char ROGUE = '@';    // Name of the rogue
    private int N;               // Board dimension
    private Site monsterSite;    // Location of monster
    private Site rogueSite;      // Location of rogue
    private Monster monster;     // The monster
    private Rogue rogue;         // The rogue

    // Initialize board from file
    public Game(In in) {
        // Read in data
        N = Integer.parseInt(in.readLine());
        char[][] board = new char[N][N];
        for (int i = 0; i < N; i++) {
            String s = in.readLine();
            for (int j = 0; j < N; j++) {
                board[i][j] = s.charAt(2*j);

                // Check for monster's location
                if (board[i][j] >= 'A' && board[i][j] <= 'Z') {
                    MONSTER = board[i][j];
                    board[i][j] = '.';
                    monsterSite = new Site(i, j);
                }

                // Check for rogue's location
                if (board[i][j] == ROGUE) {
                    board[i][j] = '.';
                    rogueSite  = new Site(i, j);
                }
            }
        }
        dungeon = new Dungeon(board);
        monster = new Monster(this);
        rogue   = new Rogue(this);
    }

    // Return position of monster and rogue
    public Site getMonsterSite() { return monsterSite; }
    public Site getRogueSite()   { return rogueSite;   }

    // Return the dungeon structure
    public Dungeon getDungeon()  { return dungeon;     }

    // Play until the monster catches the rogue
    public void play() {
        for (int t = 1; true; t++) {
            System.out.println("Move " + t);
            System.out.println();

            // Monster moves
            if (monsterSite.equals(rogueSite)) break;
            Site next = monster.move();
            if (dungeon.isLegalMove(monsterSite, next)) monsterSite = next;
            else throw new RuntimeException("Monster caught cheating");
            System.out.println(this);

            // Rogue moves
            if (monsterSite.equals(rogueSite)) break;
            next = rogue.move();
            if (dungeon.isLegalMove(rogueSite, next)) rogueSite = next;
            else throw new RuntimeException("Rogue caught cheating");
            System.out.println(this);
        }

        System.out.println("Caught by monster");

    }

    // String representation of game state
    public String toString() {
        String s = "";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Site site = new Site(i, j);
                if (rogueSite.equals(monsterSite) && (rogueSite.equals(site))) s += "* ";
                else if (rogueSite.equals(site))                               s += ROGUE   + " ";
                else if (monsterSite.equals(site))                             s += MONSTER + " ";
                else if (dungeon.isRoom(site))                                 s += ". ";
                else if (dungeon.isCorridor(site))                             s += "+ ";
                else if (dungeon.isRoom(site))                                 s += ". ";
                else if (dungeon.isWall(site))                                 s += "  ";
            }
            s += NEWLINE;
        }
        return s;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the absolute path of the file：");
        String filePath = scanner.nextLine();
        In in = new In(filePath);
        Game game = new Game(in);
        game.play();
    }
}