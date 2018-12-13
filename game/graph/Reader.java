package game.graph;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;

public class Reader {
    public static GraphData readGraph(int graphNumber) {
        return readGraph(String.format("game/Graphs/graph%02d.txt", graphNumber));
    }
    
    public static GraphData readGraph(String fileName) {
        return readGraph(new File(fileName));
    }
    
    public static GraphData readGraph(File file) {
        int nNodes = 0;
        int nEdges = 0;
        int seenEdges = 0;
                                            
        var edges = new ArrayList<int[]>();
        
        try {
            String path = file.getCanonicalPath();
            System.out.println("path: " + path);
            
            var reader = new FileReader(path);
            var buffer = new BufferedReader(reader);
            
            System.out.println("reader and buffer initialized");
            
            String line;
            
            while ((line = buffer.readLine()) != null) {
                if (line.startsWith("//")) continue; // ignore commented lines
                else if (line.startsWith("VERTICES = "))
                    nNodes = Integer.parseInt(line.substring(11));
                else if (line.startsWith("EDGES = "))
                    nEdges = Integer.parseInt(line.substring(8));
                else {
                    String[] edgeStr = line.split(" ");
                    var edge = new int[] {
                        Integer.parseInt(edgeStr[0]) - 1, // nodes start from 0
                        Integer.parseInt(edgeStr[1]) - 1
                    };
                    edges.add(edge);
                    
                    seenEdges++;
                }
            }
        } catch (IOException e) {
            System.err.println("error: couldn't read file");
            System.err.println(e);
            System.exit(1);
        }
        
        if (seenEdges != nEdges)
            throw new RuntimeException("error: loading file, declared number of edges doesn't match actual number of edges");
        
        var edgeArray = edges.toArray(new int[edges.size()][2]);
        var data = new GraphData(nNodes, edgeArray);
        return data;
    }
}