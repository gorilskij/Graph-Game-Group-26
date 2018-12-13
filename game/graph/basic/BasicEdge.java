package game.graph.basic;

public class BasicEdge<NodeT extends BasicNode> {
    public NodeT a;
    public NodeT b;
    
    public boolean linked(NodeT node) {
        return a == node || b == node;
    }
}