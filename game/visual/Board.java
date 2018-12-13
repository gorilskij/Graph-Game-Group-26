package game.visual;

import game.useful.Tools;
import game.useful.GoodList;
import game.graph.Node;
import game.graph.Edge;
import game.graph.GraphData;
import game.graph.solve.Graph;
import game.menus.WindowManager;
import game.menus.DoneMethods;
import game.menus.Selection;

import java.util.ArrayList;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

class Mutable<T> {
    private T value = null;
    public Mutable() {}
    public Mutable(T v) {value = v;}
    public boolean hasValue() {return value != null;}
    public T getValue() {return value;}
    public void setValue(T v) {value = v;}
}

public class Board extends JPanel {
    private Dimension size = new Dimension(900, 700);
    
    private int border = 50;
    
    public final int gameMode;
    
    public WindowManager manager;
    
    public GraphData data;
    public final ColorPicker picker;
    public final History history;
    
    public final Thread completeSolutionThread;
    private final Mutable<Graph> completeSolution;
    public boolean hasCompleteSolution() {return completeSolution.hasValue();}
    public Graph getCompleteSolution() {return completeSolution.getValue();}
    public ActionListener doneCall = null;
    public Graph completeSolution() {
        if (completeSolution.hasValue()) {
            System.out.println("completeSolution has value");
            return completeSolution.getValue();
        }
        
        var window = new Selection("Waiting...", manager);
        window.addLabel("Waiting for a solution to the graph to be found.");
        window.addLabel("(this means you beat the computer, congrats!)");
        var skip = new JButton("Skip");
        skip.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            completeSolutionThread.stop(); // deprecated
            if (doneCall == null) {
                System.err.println("there should really be a doneCall here");
                System.exit(1);
            }
            doneCall.actionPerformed(null);
        }});
        window.add(skip);
        window.buttonPanel.add(skip);
        manager.addWindow(window, false);
        return null;
    }
    
    public int numberOfColors() {
        // white excluded
        var cols = new ArrayList<Color>();
        for (Node node : data.nodes)
            if (!node.color.equals(Color.WHITE) && !cols.contains(node.color)) cols.add(node.color);
        return cols.size();
    }
    
    private Graph flooded = null;
    public Graph flooded() {
        if (flooded == null) flooded = new Graph(data).flood();
        return flooded;
    }
    
    public boolean allColored() {
        for (Node node : data.nodes) if (node.color.equals(Color.WHITE)) return false;
        return true;
    }
    
    public Board(GraphData d, ColorPicker p, int g, WindowManager m) {
        super();
        manager = m;

        gameMode = g;

        data = d;
        data.makeCoords(this);
        repaint();

        picker = p;
        picker.giveBoard(this);

        history = new History(this);

        setPreferredSize(size);
        setBackground(Color.black);

        addMouseListener(new MouseAdapter() {public void mousePressed(MouseEvent e) {
            clicked(e.getX(), e.getY(), e.getButton());
        }});
        addMouseMotionListener(new MouseAdapter() {public void mouseMoved(MouseEvent e) {
            moved(e.getX(), e.getY());
        }});

        // open a new thread to compute the real solution in the background
        completeSolution = new Mutable<>();
        final Graph graph = new Graph(data);
        completeSolutionThread = new Thread() {
            public void run() {
                graph.solve();
                completeSolution.setValue(graph.solution);
                System.out.println("done calculating complete solution");
                if (doneCall != null) doneCall.actionPerformed(null);
            }
        };
        System.out.println("thread>> " + completeSolutionThread);
        manager.activeThreads.add(completeSolutionThread);
        completeSolutionThread.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (Edge edge : data.edges) if (edge.style == Edge.DARK) edge.draw(g, size, border); // dark edges
        for (Node node : data.nodes) if (node.style == Node.DARK) node.draw(g, size, border); // dark nodes
        for (Edge edge : data.edges) if (edge.style != Edge.DARK) edge.draw(g, size, border); // light edges
        for (Node node : data.nodes) if (node.style != Node.DARK) node.draw(g, size, border); // light nodes
        
        // redraw currently highlighted node to bring it to front
        for (Node node : data.nodes) if (node.style == Node.HIGHLIGHTED) {
            node.draw(g, size, border);
            break;
        }
    }
    
    public boolean locked = false;
    public int bestCN = -1;
    private void clicked(int x, int y, int button) {
        if (locked) return;
        var node = data.whichNode(new Point(x, y), size, border);
        if (node == null) return;
        
        if (button == MouseEvent.BUTTON1) { // left click
            boolean changed = history.setColor(node, picker.storedColor);
            if (changed) {
                solution = null;
                if (gm3order != null) gm3Advance();
                
                if (data.allColored() != allColored())
                    throw new RuntimeException("allColored mismatch Board != GraphData");
                if (allColored()) {
                    if (gameMode == 2) {
                        int cs = solution().nColors;
                        if (cs > bestCN) bestCN = cs;
                    }
                }
            }
        } else if (button == MouseEvent.BUTTON3) { // right click
            boolean changed = history.clearColor(node);
            if (changed) solution = null;
        }
        
        checkDoneButton();
        
        repaint();
    }
    
    public void clear() {
        for (Node node : data.nodes)
            history.clearColor(node, 1);
        repaint();
    }
    
    public void checkDoneButton() {
        picker.checkDoneButton();
    }
	
    private void moved(int x, int y) {
        var node = data.whichNode(new Point(x, y), size, border);
        
        if (node == null) {
            for (Node n : data.nodes) {
                n.style = Node.NORMAL;
                if (n.gm3status == Node.GM3_MY) n.gm3status = n.storedgm3status;
            }
            for (Edge e : data.edges) e.style = Edge.NORMAL;
        } else
            node.highlight(picker.highContrast);
        
        repaint();
    }
    
    public void removeColor(Color color) {
        boolean any = false;
        for (Node node : data.nodes) if (color.equals(node.color)) {
            history.deleteColor(node);
            any = true;
        }
        
        history.removeColor(color);
        repaint();
        
        if (any) solution = null;
    }
    
    // solution is set back to null every time a node changes color
    private Graph solution = null;
    public void clearSolution() {
        solution = null;
    }
    public Graph solution() {
        if (solution == null) {
            // recalculate if null
            Graph graph = new Graph(data);
            graph.solve();
            solution = graph.solution;
        }
        return solution;
    }
    
    public void setMySolution() {
        // might hang
        var solution = solution();
        for (int i = 0; i < solution.nodes.length; i++)
            history.setColor(data.nodes[i], solution.colorOrder[solution.nodes[i].color], 2);
        repaint();
    }
    
    private GoodList<Node> gm3order = null;
    // set up game mode 3 (special styling and behavior)
    public void initiateGameMode3() {
        picker.buttonSubPanel.remove(picker.clear);
        picker.buttonSubPanel.remove(picker.done);
        picker.buttonSubPanel.remove(picker.undo);
        picker.buttonSubPanel.remove(picker.redo);
        for (Edge edge : data.edges) edge.gm3 = true;
        var toadd = new ArrayList<Node>();
        for (Node node : data.nodes) {
            node.gm3status = Node.GM3_OFF;
            toadd.add(node);
        }
        
        // the random order of the nodes
        gm3order = new GoodList<>();
        for (int i = 0; i < data.nodes.length; i++)
            gm3order.add(toadd.remove((int) (Math.random() * toadd.size())));
        
        gm3Advance();
    }
    
    public void gm3Advance() {
        for (Node node : data.nodes) if (node.gm3status == Node.GM3_ON)
            node.gm3status = Node.GM3_OFF;
        
        if (gm3order.isEmpty()) gm3Done();
        
        gm3order.shift().gm3status = Node.GM3_ON;
        moved(-10000, -10000);
    }
    
    public void gm3Done() {
        for (Node n : data.nodes) n.gm3status = Node.NOT_GM3;
        for (Edge e : data.edges) e.gm3 = false;
        repaint();
        picker.done.doClick();
    }
}