package game.graph;

import game.visual.Board;
import game.graph.basic.BasicGraphData;

import java.util.ArrayList;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;

public class GraphData extends BasicGraphData<Node, Edge> {
    public GraphData(int nNodes, int[][] edgesIn) {
        nodes = new Node[nNodes];
        edges = new Edge[edgesIn.length];
        
        for (int i = 0; i < nNodes; i++) nodes[i] = new Node();
        
        for (int i = 0; i < edgesIn.length; i++) {
            var edge = new Edge();
            edge.a = nodes[edgesIn[i][0]];
            edge.b = nodes[edgesIn[i][1]];
            edges[i] = edge;
        }
        
        for (Node node : nodes) {
            var linkedEdges = new ArrayList<Edge>();
            var unlinkedEdges = new ArrayList<Edge>();
            for (Edge edge : edges) {
                if (edge.linked(node)) linkedEdges.add(edge);
                else unlinkedEdges.add(edge);
            }
            node.myEdges = linkedEdges.toArray(new Edge[linkedEdges.size()]);
            node.otherEdges = unlinkedEdges.toArray(new Edge[unlinkedEdges.size()]);
            node.myNodes = new Node[node.myEdges.length];
            for (int i = 0; i < node.myEdges.length; i++) {
                var edge = node.myEdges[i];
                node.myNodes[i] = edge.a == node ? edge.b : edge.a;
            }
            node.otherNodes = new Node[nodes.length - node.myNodes.length - 1];
            int index = 0;
            for (Node candidate : nodes)
                if (node != candidate && !node.linked(candidate)) node.otherNodes[index++] = candidate;
            
            for (Node check : node.otherNodes)
            if (check == node) {
                System.err.println("BAD");
                System.exit(4);
            }
        }
    }
    
    public void makeCoords(Board board) {
        Positioner.createCoords(this, board);
    }
    
    public Node whichNode(Point clicked, Dimension size, int border) {        
        for (Node node : nodes)
            if (node.isMe(clicked, size, border)) return node;
        return null;
    }
    
    public boolean allColored() {
        for (Node node : nodes) if (node.color.equals(Color.WHITE)) return false;
        return true;
    }
}