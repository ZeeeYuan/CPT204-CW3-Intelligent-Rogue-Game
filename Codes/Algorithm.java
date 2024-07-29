import java.util.ArrayList;
import java.util.List;

public class Algorithm extends AlgorithmBase{
    Site[] visitOrder; // Sites to be visited
    int[] dx = {1, 0,0,  -1,1,1,-1,-1},dy={0, 1, -1, 0,1,-1,1,-1}; // Eight directions
    public int[][] distance; // Distance to the monster
    public Site[][] prev; // Previous node of each site
    int n; // Dungeon size
    private Dungeon dungeon;
    Site monster;
    Site rogue;
    private List<Site> circle = new ArrayList<>(); // Circle path
    private List<Site> best = new ArrayList<>(); // Optimal route
    int passHallway = 0; // The number of passed corridors
    boolean[][] visited; // Visiting status of each site
    int minix = 0x3f3f3f3f; // Pivot for comparison of the shortest path
    boolean found = false; // Whether find the best route

    public Algorithm(Game game){
        this.dungeon = game.getDungeon();
        this.n = dungeon.size();
        distance = new int[n][n];
        prev = new Site[n][n];
        visitOrder = new Site[n*n];
        visited = new boolean[n][n];
        monster = game.getMonsterSite();
        rogue = game.getRogueSite();
        minix = 0x3f3f3f3f;
        found = false;
        circle = new ArrayList<>();
        best = new ArrayList<>();
    }

    // Refresh the game state
    public void flush(){
        minix = 0x3f3f3f3f;
        found = false;
        circle = new ArrayList<>();
        best = new ArrayList<>();
    }

    // BFS implementation
    public List<Site> bfs(Site monster, Site rogue){
        int head = 0, tail = 0; // Position of the head and tail of the visiting queue
        distance = new int[n][n];
        prev = new Site[n][n];
        visitOrder = new Site[n*n];
        visitOrder[tail++] = monster; // Start visiting from the monster position
        // Initialize the distance
        for (int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                distance[i][j] = -1;
            }
        }

        distance[monster.i()][monster.j()] = 0;

        while(head < tail){ // If queue is empty
            Site site = visitOrder[head++]; // Extract the site to be visited and move the pointer
            // Traverse eight directions
            for(int i = 0; i < 8; i++){
                int x = dx[i] + site.i(), y = site.j() + dy[i];
                Site move = new Site(x,y);
                if (dungeon.isLegalMove(site,move) && distance[x][y] == -1){
                    distance[x][y] = distance[site.i()][site.j()] + 1;
                    prev[x][y] = site;
                    visitOrder[tail++] = move;
                }
            }
        }
        // Backtracking from the rogue to monster to record the path
        List<Site> path = new ArrayList<>();
        int x = rogue.i(), y = rogue.j();
        if(distance[x][y] != -1){
            path.add(0, rogue);
            while(x != monster.i() || y != monster.j()){
                Site pre = prev[x][y];
                if(pre.i() != monster.i() || pre.j() != monster.j()){
                    path.add(0, pre);
                }
                x = pre.i();
                y = pre.j();
            }
        }

        return path;
    }

    // DFS implementation
    public void dfs(Site cur, int depth){
        // Pruning for too deep recursion
        if(depth > 50){
            found = true;
            best = new ArrayList<>();
            return;
        }
        // Find the best path successfully
        if(found){
            return;
        }
        // Update the best cyclic path
        if(cur.equals(rogue) && passHallway > 0 && circle.size() > 2){
            // Pruning for best path update
            if(depth < minix){
                minix = depth;
                best.addAll(circle);
                found = true;
            }
            return;
        }
        // Pruning for redundant traversal: only detect four directions
        for(int i = 0; i < 4; i++){
            int x = cur.i() + dx[i];
            int y = cur.j() + dy[i];
            Site move = new Site(x,y);
            if ((dungeon.isLegalMove(cur,move) && visited[x][y] == false)){
                if(dungeon.isCorridor(move)){
                    passHallway++;
                }
                visited[x][y] = true;
                circle.add(move);
                // Recursive call to detect by depth
                dfs(move,depth+1);
                // Backtracking
                if(dungeon.isCorridor(move)){
                    passHallway--;
                }
                circle.remove(circle.size() - 1);
                visited[x][y] = false;
            }
        }
    }

    // Return the optimal path
    public List<Site> getBest(){
        return this.best;
    }
}
