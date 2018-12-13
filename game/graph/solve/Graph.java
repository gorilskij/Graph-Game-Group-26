package game.graph.solve;

import game.graph.basic.BasicGraphData;
import game.graph.basic.BasicNode;
import game.graph.basic.BasicEdge;
import game.graph.GraphData;
import game.graph.Node;
import game.visual.ColorPrecedence;

import java.awt.Color;

import java.util.ArrayList;

public class Graph extends BasicGraphData<SNode, SEdge> {
    public Color[] colorOrder = null;
    // this stores the relationships between visual colors (if contained in input GraphData object) and the 0,1,... colors used by Graph, SNode and SEdge
    
    // this constructor accepts a GraphData argument to convert it to Graph
    // or a Graph argument to clone it into a new Graph
    public <DataT extends BasicGraphData<? extends BasicNode, ? extends BasicEdge>> Graph (DataT dataIn) {
        var data = dataIn.getBasic();
        
        nodes = new SNode[data.nodes.length];
        for (int i = 0; i < nodes.length; i++) nodes[i] = new SNode();
        edges = new SEdge[data.edges.length];
        
        for (int i = 0; i < edges.length; i++) {
            var edge = new SEdge();
            edge.a = nodes[data.indexOfNode(data.edges[i].a)];
            edge.b = nodes[data.indexOfNode(data.edges[i].b)];
            edges[i] = edge;
        }
        
        if (dataIn instanceof GraphData) {
            for (int i = 0; i < data.nodes.length; i++) {
                var node = nodes[i];
                var oldNode = data.nodes[i];
                node.myNodes = new SNode[oldNode.myNodes.length];
                node.myNodeIndices = new int[node.myNodes.length];
                for (int j = 0; j < node.myNodes.length; j++) {
                    int index = data.indexOfNode(oldNode.myNodes[j]);
                    node.myNodes[j] = nodes[index];
                    node.myNodeIndices[j] = index;
                }
            
                node.otherNodes = new SNode[oldNode.otherNodes.length];
                node.otherNodeIndices = new int [node.otherNodes.length];
                for (int j = 0; j < node.otherNodes.length; j++) {
                    int index = data.indexOfNode(oldNode.otherNodes[j]);
                    node.otherNodes[j] = nodes[index];
                    node.otherNodeIndices[j] = index;
                }
            
                node.myEdges = new SEdge[oldNode.myEdges.length];
                node.myEdgeIndices = new int[node.myEdges.length];
                for (int j = 0; j < node.myEdges.length; j++) {
                    int index = data.indexOfEdge(oldNode.myEdges[j]);
                    node.myEdges[j] = edges[index];
                    node.myEdgeIndices[j] = index;
                }
            
                node.otherEdges = new SEdge[oldNode.otherEdges.length];
                node.otherEdgeIndices = new int[node.otherEdges.length];
                for (int j = 0; j < node.otherEdges.length; j++) {
                    int index = data.indexOfEdge(oldNode.otherEdges[j]);
                    node.otherEdges[j] = edges[index];
                    node.otherEdgeIndices[j] = index;
                }
            }
        } else if (dataIn instanceof Graph) {
            var dataGraph = (Graph) dataIn;
            for (int i = 0; i < dataGraph.nodes.length; i++) {
                var node = nodes[i];
                var oldNode = dataGraph.nodes[i];
                node.myNodeIndices = oldNode.myNodeIndices;
                node.myNodes = new SNode[node.myNodeIndices.length];
                for (int j = 0; j < node.myNodes.length; j++)
                    node.myNodes[j] = nodes[node.myNodeIndices[j]];
                
                node.otherNodeIndices = oldNode.otherNodeIndices;
                node.otherNodes = new SNode[node.otherNodeIndices.length];
                for (int j = 0; j < node.otherNodes.length; j++)
                    node.otherNodes[j] = nodes[node.otherNodeIndices[j]];
                
                node.myEdgeIndices = oldNode.myEdgeIndices;
                node.myEdges = new SEdge[node.myEdgeIndices.length];
                for (int j = 0; j < node.myEdges.length; j++)
                    node.myEdges[j] = edges[node.myEdgeIndices[j]];
                
                node.otherEdgeIndices = oldNode.otherEdgeIndices;
                node.otherEdges = new SEdge[node.otherEdgeIndices.length];
                for (int j = 0; j < node.otherEdges.length; j++)
                    node.otherEdges[j] = edges[node.otherEdgeIndices[j]];
            }
        } else {
            System.err.println("error");
            System.exit(1);
        }
        
        if (dataIn instanceof Graph) {
            // extract data from Graph object
            var dataGraph = (Graph) dataIn;
            setNColors(dataGraph.nColors);
            for (int i = 0; i < nodes.length; i++) nodes[i].extract(dataGraph.nodes[i]);
            colorOrder = dataGraph.colorOrder;
        } else if (dataIn instanceof GraphData) {
            // recover visual colors from GraphData object and convert to int scale, also set nColors
            var dataGData = (GraphData) dataIn;
            
            var vColors = new ArrayList<Color>();
            for (int i = 0; i < dataGData.nodes.length; i++) {
                var color = dataGData.nodes[i].color;
                if (!Color.WHITE.equals(color) && !vColors.contains(color)) vColors.add(color);
            }
            
            for (int i = 0; i < nodes.length; i++)
                nodes[i].color = vColors.indexOf(dataGData.nodes[i].color);
            
            setNColors(nodes.length); // for safety, so no nodes will be coerced
            for (SNode node : nodes) node.reevaluate();
            
            colorOrder = vColors.toArray(new Color[vColors.size()]);
        }
    }
    
    public Integer nColors = null;
    public void setNColors(Integer newNColors) {
        if (newNColors == null) return;
        nColors = newNColors;
        for (SNode node : nodes) node.setNColors(newNColors);
    }
    
    public boolean isValid() {
        for (SNode node : nodes) for (SNode other : node.myNodes)
            if (node.color >= 0 && node.color == other.color) return false;
        return true;
    }
    
    public boolean isSolved() {
        for (SNode node : nodes) if (node.color < 0) return false;
        if (!isValid()) return false;
        return true;
    }
    
    public int[] maxClique() { // returns array of indices
        ArrayList<SNode> stored = null;
        for (int i = 1; i <= nodes.length; i++) {
            System.out.println("trying " + i + "-clique");
            if (stored != null) {
                // try expanding previous clique
                System.out.println("extending");
                var extended = subClique(i, stored, new ArrayList<SNode>(), System.nanoTime());
                if (extended != null) {
                    stored = extended;
                    System.out.println("extension success");
                    System.out.println();
                    continue;
                }
                System.out.println("extension failure");
                System.out.println();
            }
            var next = subClique(i, new ArrayList<SNode>(), new ArrayList<SNode>(), System.nanoTime());
            if (next == null) break; // return stored
            else stored = next;
        }
        var array = new int[stored.size()];
        for (int i = 0; i < stored.size(); i++) {
            for (int j = 0; j < nodes.length; j++) {
                if (stored.get(i) == nodes[j]) {
                    array[i] = j;
                    break;
                }
            }
        }
        return array;
    }
    public ArrayList<SNode> subClique(int size, ArrayList<SNode> sofar, ArrayList<SNode> excl, long start) {
        if (System.nanoTime() - start > 1e9) return null; // stop after 1 second
        if (sofar.size() == size) return sofar;
        var myExcl = new ArrayList<SNode>(excl);
        outer: for (SNode node : nodes) {
            if (node.myNodes.length < size - 1) continue; // ignore nodes with not enough edges
            if (sofar.contains(node)) continue;
            if (myExcl.contains(node)) continue;
            for (SNode stored : sofar) if (!node.linked(stored)) continue outer;
            var newSofar = new ArrayList<SNode>(sofar);
            newSofar.add(node);
            var attempt = subClique(size, newSofar, myExcl, start);
            if (attempt != null) return attempt;
            // no cliques with this node exist
            myExcl.add(node);
        }
        return null;
    }
    
    public boolean solved = false;
    public Graph solution = null;
    public void solve() {
        if (solved) {
            System.err.println("warning: attempting to re-solve Graph");
            return;
        }
        
        System.out.println("calculating max clique");
        var clique = maxClique();
        
        var flooded = subFlood(new Graph(this));
        System.out.println("flooded: " + flooded.nColors);
        
        // check if solving from input
        int inputColors = 0;
        for (SNode node : nodes) if (node.color >= 0) inputColors++;
        
        // start from what you have
        int startColors = inputColors == 0 ? clique.length : Math.max(clique.length, inputColors);
        
        if (nColors == null) throw new RuntimeException("nColors null");
        
        colorLoop:
        for (int colors = startColors; solution == null; colors++) {
            if (colors == flooded.nColors) {
                
                if (nColors == -1) {
                    // only if no colors given at start
                    solution = flooded;
                    break;
                }
            }
            
            if (colors > nodes.length) {
                throw new RuntimeException("colors > nodes, error");
            }
            
            System.out.println("trying with " + colors + " colors");
            var solving = new Graph(this);
            
            solving.setNColors(colors);
            
            boolean empty = true;
            for (SNode node : solving.nodes) if (node.color >= 0) {
                empty = false;
                break;
            }
            if (empty) {
                int mostConnections = -1;
                SNode mostConnected = null;
                for (SNode node : solving.nodes) if (node.myNodes.length > mostConnections) {
                    mostConnections = node.myNodes.length;
                    mostConnected = node;
                }
                mostConnected.color = 0;
                for (SNode node : solving.nodes) node.reevaluate();
                for (SNode node : solving.nodes) if (node.color < 0 && node.allowed == null) {
                    // nColors is not enough
                    System.out.println("skipping due to nColors inadequacy");
                    continue colorLoop;
                }
            }
            
            try {
                for (SNode node : solving.nodes) if (node.myNodes.length == 0) node.setColor(0);
            } catch (ColorConflict e) {
                throw new RuntimeException(e);
            }
            
            solution = subSolve(solving);
        }
        
        // remake colorOrder
        var newOrder = new Color[solution.nColors];
        for (int i = 0; i < newOrder.length; i++) {
            if (i < colorOrder.length) newOrder[i] = colorOrder[i]; // preserve previously assigned
            else {
                // find first unmentioned color
                cLoop: for (Color color : ColorPrecedence.colors) {
                    for (Color check : newOrder) {
                        if (check == null) break;
                        if (check == color) continue cLoop;
                    }
                    
                    // found color
                    newOrder[i] = color;
                    break;
                }
            }
        }
        solution.colorOrder = newOrder;
    }
    
    public Graph flood() {
        return subFlood(new Graph(this));
    }
    
    private Graph subFlood(Graph graph) {
        if (graph.isSolved()) return graph;
        int colors = 1;
        graph.setNColors(colors);
        for (int n = 0; n < graph.nodes.length; n++) {
            var node = graph.nodes[n];
            wh: while (true) {
                var forbidden = new ArrayList<Integer>();
                for (SNode other : node.myNodes) if (other.color >= 0) forbidden.add(other.color);
                if (forbidden.size() >= graph.nColors) {
                    graph.setNColors(++colors);
                    continue wh;
                }
                for (int i = 0;; i++) {
                    if (!forbidden.contains(i)) {
                        node.color = i;
                        break wh;
                    }
                }
            }
        }
        return graph;
    }
    
    private Graph subSolve(Graph graph) {
        return subSolve(graph, 0);
    }
    private Graph subSolve(Graph graph, int depth) {
        System.out.println("DEPTH: " + depth);
        
        if (graph.isSolved()) return graph;
        
        for (int n = 0; n < graph.nodes.length; n++) {
            var node = graph.nodes[n];
            if (node.color >= 0) continue;
            for (int c : node.allowed) {
                var next = new Graph(graph);
                try {
                    next.nodes[n].setColor(c); // ColorConflict thrown here
                    var attempt = subSolve(next, depth + 1);
                    if (attempt != null) return attempt;
                } catch (ColorConflict e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}