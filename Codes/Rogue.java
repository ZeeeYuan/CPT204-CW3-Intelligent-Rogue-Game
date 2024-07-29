import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rogue{
    private Game game;
    private Dungeon dungeon;
    private int N;
    private AlgorithmBase algorithm;
    private AlgorithmFactory factory;
    List<Site> best = null;
    private static boolean inCircle=false;
    public Rogue(Game game) {
        factory = new AlgorithmFactory(game);
        this.game = game;
        this.dungeon = game.getDungeon();
        this.N = dungeon.size();
        algorithm = factory.createAlgorithm();
    }
    // The position of the rogue in the circle
    private static int position = 0;
    // If the dungeon has a corridor-based circle
    private static boolean selfCircle = false;
    // Take an optimal move
    public Site move() {
        Site monster = game.getMonsterSite();
        Site rogue = game.getRogueSite();
        Site move = rogue;
        // Assume the corridor is a part of the circle, which can avoid the bug through size judgement
        if(dungeon.isCorridor(rogue)){
            inCircle = true;
        }
        boolean circleFound = false;
        algorithm.flush();
        if(position == 0 && (inCircle == false || selfCircle == true)){
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    Site site = new Site(i, j);
                    // Assume the dungeon has a circle if it has corridors
                    if (dungeon.isCorridor(site)){
                        circleFound = true;
                    }
                }
                if(circleFound){
                    break;
                }
            }

            if(circleFound){
                algorithm.dfs(rogue, 1);
                best = algorithm.getBest();
                System.out.println(best.size());
            }else{
                best = new ArrayList<>();
            }
        }

        // The path exists
        if(best.size()>0){
            List<Site> newBest = new ArrayList<>();
            boolean inCorridor = false;
            boolean loopFormed = false;
            // Entrance and exit of the corridor
            Site in = null;
            Site out = null;
            if(dungeon.isRoom(rogue) == true && (inCircle == false || selfCircle == true)){
                for(Site site : best){
                    if (dungeon.isRoom(site) && inCorridor == true && loopFormed == false){
                        if(algorithm.bfs(newBest.get(newBest.size() - 1), in).size() == 1){
                            selfCircle = true;
                            break;
                        }
                        out = site;
                        newBest.add(site);
                        // Replace the original path with the optimal route from the rogue to the cycle with its entrance and exit
                        newBest.addAll(algorithm.bfs(site, rogue));
                        inCorridor = false;
                        break;
                    }
                    if(dungeon.isCorridor(site) && inCorridor == true){
                        newBest.add(site);
                    }
                    if (dungeon.isCorridor(site) && inCorridor == false){
                        newBest.addAll(0,algorithm.bfs(rogue, site));
                        in = site;
                        inCorridor = true;
                    }
                }
                // If the distance to the exit is less than that to the entrance, reverse it
                if(out != null && algorithm.bfs(rogue, out).size() < algorithm.bfs(rogue, in).size()){
                    Collections.reverse(newBest);
                    newBest.add(newBest.get(0));
                    newBest.remove(0);
                }
                best = newBest;
                position = 0;


            }

            // The best path is legal and reliable
            if((algorithm.bfs(best.get(position%best.size()), monster).size() >= algorithm.bfs(rogue, monster).size() && algorithm.bfs(rogue, monster).size() > 1) || algorithm.bfs(best.get(position%best.size()), monster).size() > 1){
                move = best.get(position%best.size());
                position++;
            }else{
                // Evade as far as possible otherwise
                position = 0;
                List<Site> dist = algorithm.bfs(rogue, monster);

                int[] dx = {-1, 0, 1, 0, 1, 1, -1, -1}, dy = {0, 1, 0, -1, 1, -1, 1, -1};
                int remote = dist.size();
                for(int i = 0; i < 8; i++){
                    int x = dx[i] + rogue.i(), y = rogue.j() + dy[i];
                    Site t = new Site(x,y);
                    if(dungeon.isLegalMove(rogue, t)){
                        int dist_t = algorithm.bfs(t, monster).size();
                        if (dist_t >= remote){
                            remote = dist_t;
                            move = t;
                        }
                    }
                }
            }
        }else {
            // If the best path does not exist, evade
            List<Site> dist = algorithm.bfs(rogue, monster);
            int[] dx = {-1, 0, 1, 0, 1, 1, -1, -1}, dy = {0, 1, 0, -1, 1, -1, 1, -1};
            int remote = dist.size();
            for(int i = 0; i < 8; i++){
                int x = dx[i] + rogue.i(), y = rogue.j() + dy[i];
                Site t = new Site(x,y);
                if(dungeon.isLegalMove(rogue, t)){
                    int dist_t = algorithm.bfs(t, monster).size();
                    if (dist_t >= remote){
                        remote = dist_t;
                        move = t;
                    }
                }
            }
        }

        return move;
    }
}