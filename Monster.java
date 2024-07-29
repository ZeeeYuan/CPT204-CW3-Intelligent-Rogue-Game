import java.util.List;

public class Monster{
    private Game game;
    private Dungeon dungeon;
    private int N;
    private AlgorithmBase algorithm;
    private AlgorithmFactory factory;
    public Monster(Game game) {
        factory = new AlgorithmFactory(game);
        this.game = game;
        this.dungeon = game.getDungeon();
        this.N = dungeon.size();
        algorithm = factory.createAlgorithm();
    }

    // Take an optimal move towards the rogue
    public Site move() {
        Site monster = game.getMonsterSite();
        Site rogue = game.getRogueSite();
        Site move = monster;

        List<Site> path = algorithm.bfs(monster,rogue);
        int steps = path.size();
        if (steps > 0){
            move=path.get(0);
        }

        return move;
    }
}