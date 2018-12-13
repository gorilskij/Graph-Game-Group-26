package game.menus;

import game.menus.WindowManager;
import game.menus.Selection;
import game.visual.Board;
import game.visual.ColorPickerPlus;
import game.graph.Node;
import game.graph.solve.SNode;
import game.useful.Tools;

import java.util.ArrayList;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BoxLayout;

public class DoneMethods {
    public static void confirmSurrender(WindowManager manager, Board board) {
        var window = new Selection("Sure?", manager);
        
        window.addLabel("The graph isn't finished,");
        window.addLabel("are you sure you wish to surrender?");
        
        var hPanel = new JPanel();
        hPanel.setLayout(new BoxLayout(hPanel, BoxLayout.X_AXIS));
        
        var yes = new JButton("Yes");
        yes.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            manager.goBack();
            manager.queue.last().enabled();
            surrender(manager, board);
        }});
        var no = new JButton("No");
        no.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            manager.goBack();
            manager.queue.last().enabled();
        }});
        
        window.buttonPanel.add(yes);
        window.buttonPanel.add(no);
        
        manager.addWindow(window, false);
    }
    
    private static void addTimeTaken(int timeTaken, Selection window, ColorPickerPlus picker) {
        int timeGiven = picker.timeGiven;
        if (timeTaken > timeGiven) window.addLabel("You took " + Tools.timeToString(timeTaken - timeGiven) + " more than the " + Tools.timeToString(timeGiven) + " you had.");
        else if (timeTaken < timeGiven) window.addLabel("You took " + Tools.timeToString(timeGiven - timeTaken) + " less than the " + Tools.timeToString(timeGiven) + " you had.");
        else window.addLabel("You took exactly the " + Tools.timeToString(timeGiven) + " you had.");
    }
    // what happens when someone presses surrender
    public static void surrender(WindowManager manager, Board board) {
        System.out.println("SURRENDER");
        // save the time te player took
        int timeTaken = board.picker.stopTimer();
        // create window to show
        var window = new Selection("You surrendered", manager);
        // add it
        window.addLabel("You surrendered.");
        
        int nodesColored = 0;
        for (Node node : board.data.nodes) if (!node.color.equals(Color.WHITE)) nodesColored++;
        
        window.addLabel("Nodes colored: " + nodesColored);
        window.addLabel("Time taken: " + Tools.timeToString(timeTaken));
        if (board.gameMode == 2) addTimeTaken(timeTaken, window, (ColorPickerPlus) board.picker);
        
        if (!board.hasCompleteSolution()) window.addLabel("Sorry, couldn't calculate the chromatic number in time");
        else addChromaticInfo(window, manager, board, false);
        // add another button
        var ok = new JButton("View solution");
        ok.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            manager.goBack();
            board.setMySolution();
            board.locked = true;
            var game = manager.queue.last();
            game.mainPanel.remove(((Game) game).colorPicker);
            game.mainPanel.remove(((Game) game).tjp);
            game.pack();
            game.revalidate();
            game.repaint();
        }});
        window.buttonPanel.add(ok);
        
        window.neverClose();
        manager.addWindow(window, false);
    }
    // what happens when the timer runs out
    public static void timeOut(WindowManager manager, Board board, int timeGiven) {
        System.out.println("TIME OUT");
        int timeTaken = board.picker.stopTimer();
        
        var window = new Selection("Time out!", manager);
        
        window.addLabel("Your " + Tools.timeToString(timeGiven) + " have run out.");
        
        int nodesColored = 0;
        for (Node node : board.data.nodes) if (!node.color.equals(Color.WHITE)) nodesColored++;
        
        var solution = board.solution();
        var completeSolution = board.completeSolution();
        
        window.addLabel("Nodes colored: " + nodesColored);
        
        if (!board.hasCompleteSolution()) window.addLabel("Sorry, couldn't calculate the chromatic number in time");
        else {
            if (nodesColored < board.data.nodes.length) { // not currently solved
                window.addLabel("The current state isn't a solution");
                if (board.bestCN < 0) {
                    window.addLabel("You haven't completed the graph so far.");
                    window.addLabel("The best you could have done is " + completeSolution.nColors);
                    window.addLabel("The best you can do starting from the current state is " + solution.nColors);
                } else if (board.bestCN == completeSolution.nColors) {
                    window.addLabel("You've reached the real chromatic number (" + completeSolution.nColors + ")");
                    window.addLabel("Congratulations!");
                }
            } else {
                window.addLabel("The current state has chromatic number " + solution.nColors);
                window.addLabel("The best you could have done is " + completeSolution.nColors);
            }
        }
        
        var ok = new JButton("View solution");
        ok.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            manager.goBack();
            board.setMySolution();
            board.locked = true;
            var game = manager.queue.last();
            game.mainPanel.remove(((Game) game).colorPicker);
            game.mainPanel.remove(((Game) game).tjp);
            game.pack();
            game.revalidate();
            game.repaint();
        }});
        window.buttonPanel.add(ok);
        
        window.neverClose();
        manager.addWindow(window, false);
    }
    
    private static void finished(WindowManager manager, Board board) {
        int timeTaken = board.picker.stopTimer();
        
        var window = new Selection("You finished", manager);
        
        window.addLabel("Congratulations!");
        window.addLabel("You finished the graph.");
        window.addLabel("Time taken: " + Tools.timeToString(timeTaken));
        if (board.gameMode == 2) addTimeTaken(timeTaken, window, (ColorPickerPlus) board.picker);
        
        window.addMainMenuButton();
        window.addExitButton();
        
        window.neverClose();
        manager.addWindow(window, false);
    }
    
    private static void finalized(WindowManager manager, Board board) {
        var mine = board.solution();
        var real = board.completeSolution();
        int timeTaken = board.picker.stopTimer();
        
        var window = new Selection("You finished coloring the graph", manager);
        window.addLabel("Congratulations!");
        window.addLabel("You finished the graph.");
        window.addLabel("Time taken: " + Tools.timeToString(timeTaken));
        if (board.gameMode == 2) addTimeTaken(timeTaken, window, (ColorPickerPlus) board.picker);
        
        addChromaticInfo(window, manager, board);
        
        window.addMainMenuButton();
        window.addExitButton();
        manager.addWindow(window, false);
        window.neverClose();
    }
    
    public static void completed(WindowManager manager, Board board) {
        var real = board.completeSolution();
        if (real == null) {
            System.out.println("real is null");
            // execute the rest after solution is found
            board.doneCall = new ActionListener() {public void actionPerformed(ActionEvent e) {
                manager.goBack();
                if (!board.hasCompleteSolution()) finished(manager, board); // can't tell you much
                else finalized(manager, board); // more information
            }};
        } else {
            System.out.println("real solution given");
            finalized(manager, board);
        }
    }
    
    private static void addChromaticInfo(Selection w, WindowManager m, Board b) {
        addChromaticInfo(w, m, b, true);
    }
    private static void addChromaticInfo(Selection window, WindowManager manager, Board board, boolean opt) {
        int colorsUsed = board.numberOfColors();
        if (!board.hasCompleteSolution()) {
            window.addLabel("Sorry, couldn't calculate the chromatic number in time");
            window.addLabel("You used " + colorsUsed + " colors");
            int flooded = board.flooded().nColors;
            if (flooded < colorsUsed) {
                window.addLabel("It was possible to solve the graph with " + flooded + " colors or fewer");
                window.addLabel("consider trying again.");
            } else
                window.addLabel("Couldn't find a solution with fewer than " + flooded + " colors");
        } else {
            var fromThis = board.solution(); // might hang
            var cs = new ArrayList<Integer>();
            for (SNode node : fromThis.nodes) if (node.color >= 0 && !cs.contains(node.color)) cs.add(node.color);
            int mine = cs.size();
            
            var complete = board.completeSolution();
            if (complete == null) throw new RuntimeException("hasCompleteSolution -> null, bad");
            int real = complete.nColors;
            
            System.out.println("mine: " + mine);
            System.out.println("real: " + real);
            System.out.println("colorsUsed: " + colorsUsed);
            
            window.addLabel("The chromatic number of the graph is " + real);
            // feedback when board is finished
            if (board.allColored()) {
                if (colorsUsed == real)
                    window.addLabel("You managed to find the chromatic number, nice!");
                else {
                    // some nice remarks
                    window.addLabel("You used " + (colorsUsed - real) + " more colors than you had to,");
                    window.addLabel("you used " + colorsUsed + " colors while you only needed " + real + ".");
                    if (colorsUsed == real + 1) window.addLabel("It's only one color, you should keep trying.");
                    else window.addLabel("Maybe if you try again you can do better.");
                    if (opt) addKeepTrying(window, manager, board);
                    if (opt) addTryAgain(window, manager, board);
                }
            } else {
                if (mine <= real) {
                    window.addLabel("You can still solve the graph without starting over,");
                    if (colorsUsed < mine)
                        window.addLabel("you'll need to use " + (mine - colorsUsed) + " more colors.");
                    else window.addLabel("but you can't use any more colors.");
                    if (opt) addKeepTrying(window, manager, board);
                } else {
                    // so mine > real
                    window.addLabel("You won't be able to reach the chromatic number like this.");
                    if (colorsUsed > real) window.addLabel("You've already exceeded the chromatic number.");
                    window.addLabel("Try changing some colors.");
                    if (opt) addKeepTrying(window, manager, board);
                    if (opt) addTryAgain(window, manager, board);
                }
            }
        }
    }
    
    private static void addKeepTrying(Selection window, WindowManager manager, Board board) {
        var keepTrying = new JButton("Keep trying");
        keepTrying.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            manager.goBack(); // keep current state
        }});
        window.buttonPanel.add(keepTrying);
    }
    
    private static void addTryAgain(Selection window, WindowManager manager, Board board) {
        var tryAgain = new JButton("Try again");
        tryAgain.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            board.clear(); // can still undo to previous state
            manager.goBack();
        }});
        window.buttonPanel.add(tryAgain);
    }
}
