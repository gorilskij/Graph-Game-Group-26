package game.graph.basic;

public class BasicGraphData<NodeT extends BasicNode, EdgeT extends BasicEdge> {
    public NodeT[] nodes;
    public EdgeT[] edges;
    
    public BasicGraphData<BasicNode, BasicEdge> getBasic() {
        // not a deep clone!
        var basicData = new BasicGraphData<BasicNode, BasicEdge>();
        basicData.nodes = (BasicNode[]) nodes;
        basicData.edges = (BasicEdge[]) edges;
        return basicData;
    }
    
    public int indexOfNode(NodeT node) {
        for (int i = 0; i < nodes.length; i++) if (nodes[i] == node) return i;
        return -1;
    }
    
    public int indexOfEdge(EdgeT edge) {
        for (int i = 0; i < edges.length; i++) if (edges[i] == edge) return i;
        return -1;
    }
}