package game.game_mode3;

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
        menu.add(sliders); //works the same way as gamemode 1
        
        menu.addSpace();
        
        var okButton = menu.addButton("Ok", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int nNodes = Tools.randInt(sliders.minNodes(), sliders.maxNodes());
				int nEdges = Tools.randInt(sliders.minEdges(), sliders.maxEdges());
                var data = Generator.makeGraph(nNodes, nEdges);
                Play.start(data, manager); //again, creates the graph with the user input
            }
        });
        
        menu.addBackButton();
        menu.addMainMenuButton();
        menu.addExitButton();
        manager.addWindow(menu);
    }
}
