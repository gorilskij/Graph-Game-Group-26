package game.graph.basic;

public class BasicNode<NodeT extends BasicNode, EdgeT extends BasicEdge> {
    public NodeT[] myNodes;
    public EdgeT[] myEdges;
    
    public NodeT[] otherNodes;
    public EdgeT[] otherEdges;
    
    public boolean linked(NodeT node) {
        for (NodeT maybe : myNodes) if (maybe == node) return true;
        return false;
    }
}