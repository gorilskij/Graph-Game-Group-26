package game.game_mode2;

import game.menus.*;
import game.graph.*;

import java.util.Hashtable;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Select {
    public static void start(WindowManager manager) {
        var menu = new Selection("Best in Time", manager);
        
        menu.addText("This game mode is for the stress-resistant people that are in our midst. The following rules apply here. You will be given an amount of time of your choosing to complete the graph. Here it is not necessary to do it perfectly, but of course you should try to do so. A clock will be counting down when the graph is created, ending the game when it reaches zero. Good luck!", 50);
        //description of the gamerules
        menu.addSep();
        
        menu.addLabel("Please choose an option");
        
        menu.addSpace();
        
        menu.addButton("Generate a random graph", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                var data = Generator.makeGraph();
                Play.start(data, 2, manager); //same as gamemode 1, creates a graph with random input, the amount of time is 30 seconds per vertex generated
            }
        });
        
        menu.addButton("Generate a random graph from parameters...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Parameters.start(manager); //again switches to the parameter menu, where the player can choose #vertices, #edges + amount of time
            }
        });
        
        menu.addButton("Load a graph from file...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu.invisible();
                String path = Chooser.chooseFile();
                menu.visible();
                if (path != null) {
                    var data = Reader.readGraph(path);
                    Play.start(data, Math.max(1, data.nodes.length / 2), manager); //works the same as in gamemode 1, also the amount of time is 30 seconds per vertex generated
                }
            }
        });
        
        menu.addBackButton();
        menu.addExitButton();
        manager.addWindow(menu);
    }
}
