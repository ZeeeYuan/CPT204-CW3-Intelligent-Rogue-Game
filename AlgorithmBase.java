import java.util.List;

// Basic abstract class for algorithm to implement
public abstract class AlgorithmBase {
    public abstract void flush(); // Refresh the game state

    public abstract List<Site> bfs(Site monster, Site rogue); // BFS to detect the shortest path

    public abstract void dfs(Site cur,int u); // DFS to find cycles

    public abstract List<Site> getBest(); // Return the optimal route
}
