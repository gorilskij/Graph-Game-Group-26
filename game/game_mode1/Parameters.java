package game.game_mode1;

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
        
        var sliders = new SliderSet();
        menu.add(sliders); //adds the vertices and edges sliders to the menu of generating a graph with user input for gamemode 1
        
        menu.addSpace();
        
        var okButton = menu.addButton("Ok", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                var data = Generator.makeGraph(sliders.randNodes(), sliders.randEdges());
                Play.start(data, manager); // creates the graph with the input of the sliders for gamemode 1
            }
        });
        
        menu.addBackButton(); // adds the button to go back to the menu of gamemode 1
        menu.addMainMenuButton(); //adds the button to go back to the main menu
        menu.addExitButton(); //adds the button to go exit the program
        manager.addWindow(menu);
    }
}
