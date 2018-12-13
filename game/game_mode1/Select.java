package game.game_mode1;

import game.menus.WindowManager;
import game.menus.Selection;
import game.graph.Generator;
import game.graph.Chooser;
import game.graph.Reader;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Select {
    public static void start(WindowManager manager) {
        var menu = new Selection("The Bitter End", manager);
        
        menu.addText("I see you are aware of your own skills. This game mode is considered the most difficult of the three. The rules are simple. You are required to color the circles as quickly as possible, but of course that is not all. Just like the title implies, you can not stop before you complete the graph perfectly. In other words, the amount of colors that you use should be the same as the chromatic number of the graph. There will be enough hints to help you, should you choose to use them. Good luck!", 50);
        // the description of the rules
        menu.addSpace();
        menu.addSep();
        
        menu.addLabel("Please choose an option");
        
        menu.addSpace();
        
        menu.addButton("Generate a random graph", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                var data = Generator.makeGraph();
                Play.start(data, manager); // starts the game and creates a graph with random input and starts a timer
            }
        });
        
        menu.addButton("Generate a random graph from parameters...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Parameters.start(manager); //switches to the parameters menu, the player can now choose #edges, #vertices
            }
        });
        
        menu.addButton("Load a graph from file...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu.invisible();
                String path = Chooser.chooseFile();
                menu.visible();
                if (path != null) {
                    var data = Reader.readGraph(path);
                    Play.start(data, manager); //hides the main menu and shows the filechooser. if filechooser is closed it will show the main menu again
                }                               // if a file has been chosen, it will try to create the graph with the information in the file
            }
        });
        
        menu.addBackButton();
        menu.addExitButton();
        manager.addWindow(menu);
    }
}
