package game.graph.solve;

// a basic exception used to stop recursion when a contradiction is reached
public class ColorConflict extends Exception {
    public ColorConflict() {
        super();
    }
    public ColorConflict(String text) {
        super(text);
    }
}