// Factory pattern for algorithm desgin
public class AlgorithmFactory {
    private Game game;

    public AlgorithmFactory(Game game) {
        this.game = game;
    }

    public AlgorithmBase createAlgorithm(){
        return new Algorithm(game);
    } // Instantiate the algorithm
}
