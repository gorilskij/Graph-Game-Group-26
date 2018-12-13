package game.game_mode1;

import game.menus.Game;
import game.menus.WindowManager;
import game.graph.GraphData;
import game.visual.ColorPrecedence;

public class Play {
    public static void start(GraphData data, WindowManager manager) {
        var game = new Game("The Bitter End", 1, data.nodes.length, manager, data); 
        
        game.standardSetup();
        manager.addWindow(game);
    }
}