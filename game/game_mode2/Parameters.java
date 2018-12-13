package game.game_mode2;

import game.useful.Tools;
import game.menus.WindowManager;
import game.menus.Selection;
import game.menus.SliderSet;
import game.graph.Generator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Parameters {
    public static void start(WindowManager manager) {
        var menu = new Selection("The Bitter End", manager);
        
        var sliders = new SliderSet(true); //adds the vertices and edges sliders to the menu of generating a graph with user input for gamemode 2
        menu.add(sliders);                  // this time it also contains a slider to choose the amount of time
        
        menu.addSpace();
        
        var okButton = menu.addButton("Ok", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                var data = Generator.makeGraph(sliders.randNodes(), sliders.randEdges());
                Play.start(data, sliders.randTime(), manager); //creates the graph with all the user input
            }
        });
        
        menu.addBackButton();
        menu.addMainMenuButton();
        menu.addExitButton();
        manager.addWindow(menu);
    }
}
