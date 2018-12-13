package game.visual;

import game.graph.Node;
import game.menus.WindowManager;
import game.menus.DoneMethods;
import game.menus.Selection;
import game.graph.solve.Graph;
import game.graph.solve.SNode;

import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import javax.swing.Timer;

public class ColorPicker extends JPanel {
    Color[] colors;
    Color storedColor;
    ColorButton[] buttons;
    Board board = null;
    JPanel colorPanel;
    JPanel buttonSubPanel;
    
    JButton undo;
    JButton redo;
    JButton clear;
    JButton done;
    JButton hint;
    // JButton solve;
	JCheckBox highContrast;
    JComponent[] actionComponents;
    
    private final Color doneButtonColor;
    
    public ColorTimer timer = new ColorTimer(1000);
    
    public ColorPicker(int nColors, JPanel cc, WindowManager manager) {
        super();
        
        if (nColors < 1) {
            System.err.println("error: not enough colors selected");
            System.exit(1);
        }
        
        colorPanel = cc;
        
        colors = new Color[nColors];
        
        for (int i = 0; i < nColors; i++) {
            if (i >= ColorPrecedence.colors.length) {
                System.err.println("error: too many colors selected");
                System.exit(1);
            }
            colors[i] = ColorPrecedence.colors[i];
        }
        
        storedColor = colors[0];
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        buttons = new ColorButton[nColors];
        
        int i = 0;
        for (Color color : colors) {
            var tcb = ColorButton.getNew(color, this);
            add(tcb);
            buttons[i++] = tcb;
        }
        
        buttonSubPanel = new JPanel();
        buttonSubPanel.setLayout(new BoxLayout(buttonSubPanel, BoxLayout.Y_AXIS));
        
        undo = new JButton("Undo");
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.history.undo();
                board.repaint();
            }
        });
        buttonSubPanel.add(undo);
        
        redo = new JButton("Redo");
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.history.redo();
                board.repaint();
            }
        });
        buttonSubPanel.add(redo);
        
        clear = new JButton("Clear");
        clear.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            board.clear();
        }});
        buttonSubPanel.add(clear);
        
        done = new JButton("Done");
        doneButtonColor = done.getBackground();
        done.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            if (board.data.allColored()) DoneMethods.completed(board.manager, board);
            else DoneMethods.confirmSurrender(board.manager, board); // warn if surrendering
        }});
        buttonSubPanel.add(done);
        
        hint = new JButton("Hint");
        hint.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            var window = new Selection("", manager);
            Graph solution = null; // stays null if not found yet
            if (board.allColored()) {
                window.addLabel("You have colored all the nodes in the graph.");
                // however / and / -> done
                window.addLabel("add chromatic number info here");
                window.addBackButton();
            } else {
                if (!board.hasCompleteSolution()) {
                    window.setTitle("Sorry");
                    window.addLabel("Sorry, couldn't calculate the chromatic number in time.");
                    window.addLabel("However, an upper bound solution was found,");
                    window.addLabel("click on \"Color a node for me\" to color a node in the graph.");
                    window.addLabel("Node that this might or might not lead to the chromatic number!");
                    window.addButton("Color a node for me", new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            manager.goBack();
                            colorNextNode(board.flooded());
                        }
                    });
                } else {
                    int colorsUsed = board.numberOfColors();
                    solution = board.solution();
                    
                    var real = board.completeSolution();
                    var cs = new ArrayList<Integer>();
                    for (SNode node : solution.nodes) if (node.color >= 0 && !cs.contains(node.color))
                        cs.add(node.color);
                    int mySolutionColors = cs.size();
                    cs = new ArrayList<Integer>();
                    for (SNode node : real.nodes) if (node.color >= 0 && !cs.contains(node.color))
                        cs.add(node.color);
                    int realSolutionColors = cs.size();
                    
                    window.setTitle("Hint");
                    
                    window.addLabel("The chromatic number of the graph is " + realSolutionColors);
                    if (mySolutionColors <= realSolutionColors) {
                        window.addLabel("You can still solve the graph with the chromatic number,");
                        if (colorsUsed < mySolutionColors) window.addLabel(
                            "you'll need to use " + (mySolutionColors - colorsUsed) + " more colors."
                        );
                        else window.addLabel("but you can't use any more colors.");
                    } else {
                        // mySolutionColors > realSolutionColors
                        window.addLabel("You won't be able to reach the chromatic number like this.");
                        if (colorsUsed > realSolutionColors) {
                            window.addLabel("You've already surpassed the chromatic number.");
                            window.addLabel("Try removing " + (colorsUsed - realSolutionColors) +
                                " colors from your graph.");
                        } else {
                            window.addLabel("Try changing around some colors, you won't be able");
                            window.addLabel("to solve the graph with the coloring you have now.");
                        }
                    }
                    
                    window.addSpace(10);
                    
                    window.addLabel("Click on \"Color a node for me\" to color a node in the graph.");
                    window.addLabel("This won't increase the minimum number");
                    window.addLabel("of colors you need to finish the graph.");
                
                    var from = solution;
                    String buttonText =
                        board.gameMode == 3 ? "Color this node for me" : "Color a node for me";
                    
                    window.addButton(buttonText, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            manager.goBack();
                            if (board.gameMode == 3) {
                                for (int i = 0; i < board.data.nodes.length; i++) {
                                    var node = board.data.nodes[i];
                                    if (node.gm3status == Node.GM3_ON) {
                                        board.history.setColor(
                                            node,
                                            from.colorOrder[from.nodes[i].color]
                                        );
                                        board.gm3Advance();
                                        break;
                                    }
                                }
                            } else colorNextNode(from);
                        }
                    });
                }
            }
            
            window.neverClose();
            board.manager.addWindow(window, false);
        }});
        buttonSubPanel.add(hint);
        
        // solve = new JButton("Solve");
        // solve.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
        //     // solve the graph and display solution
        //     System.out.println("TESTING DUMP:::DATA");
        //     System.out.println("colors:");
        //     for (Node node : board.data.nodes) System.out.println(node.color);
        //     var s = board.solution();
        //     for (int i = 0; i < s.nodes.length; i++)
        //         board.history.setColor(board.data.nodes[i], s.colorOrder[s.nodes[i].color], 2);
        //     board.repaint();
        // }});
        // buttonSubPanel.add(solve);
        
		highContrast = new JCheckBox("highlight");
		highContrast.setSelected(true);
		buttonSubPanel.add(highContrast);
        buttonSubPanel.add(new JSeparator());
        buttonSubPanel.add(new JLabel("Right click"));
        buttonSubPanel.add(new JLabel("to erase."));
        
        actionComponents = new JComponent[] {undo, redo, clear, done, hint, /*solve,*/ highContrast};
        
        add(buttonSubPanel);
        
        pickColor(colors[0]);
        
        timer.start();
        manager.activeTimers.add(timer);
    }
    
    public void colorNextNode(Graph from) { // hint
        // most connected to colored nodes
        int mostConnectedIndex = -1;
        Node mostConnected = null;
        int mostConnections = 0;
        for (int i = 0; i < board.data.nodes.length; i++) {
            if (!board.data.nodes[i].color.equals(Color.WHITE)) continue;
            int connections = 0;
            for (Node my : board.data.nodes[i].myNodes) if (!my.color.equals(Color.WHITE))
                connections++;
            if (mostConnectedIndex < 0 || connections > mostConnections) {
                mostConnectedIndex = i;
                mostConnected = board.data.nodes[i];
                mostConnections = connections;
            }
        }
        
        if (mostConnected == null) throw new RuntimeException("graph is done but you get hint, bad");
        else board.history.setColor(mostConnected, from.colorOrder[from.nodes[mostConnectedIndex].color]);
    }
    
    public void checkDoneButton() {
        boolean allColored = true;
        for (Node node : board.data.nodes) if (node.color.equals(Color.WHITE)) {
            allColored = false;
            break;
        }
        if (allColored) done.setBackground(Color.GREEN);
        else done.setBackground(doneButtonColor);
    }
    
    public void giveBoard(Board b) {board = b;}
    
    public void pickColor(Color color) {
        storedColor = color;
        for (ColorButton button : buttons) {
            if (button.color == color)
                button.select();
            else
                button.deselect();
        }
        colorPanel.setBackground(color);
    }
    
    public int stopTimer() {
        timer.stop();
        return timer.iterations;
    }
}