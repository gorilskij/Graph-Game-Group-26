package game;

import game.menus.WindowManager;
import game.menus.Selection;
import game.visual.ColorPrecedence;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // set the look and feel to nimbus (consistent on mac and win)
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("warning: could not find look and feel");
        }
        
        ColorPrecedence.fillIn(); // add an inverse for every color and randomly generated colors
        
        // create the window manager
        // this is used to manage open and hidden windows and popups
        var manager = new WindowManager();
        
        // make main window
        var menu = new Selection("Main menu", manager);
        
        menu.addText("Hello and welcome. You are about to play a game that requires great skill and cleverness. Are you ready? In this game you will be given a graph or circles connected by lines. The main goal of this game is to color the circles in such a way,  so that the least amount of colors are used. But be aware, it is not allowed to use the same color for circles that are connected by a line! There are 3 different game modes to unravel, each with a different rule set. Go and put your skills to the test with the 'Chromatic Number Game'.", 50);
        
        menu.addSep();
        menu.addLabel("Welcome, please choose a game mode");
        menu.addSpace();
        
        menu.addButton("The Bitter End", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.game_mode1.Select.start(manager);
            }
        });
        
        menu.addButton("Best Upper Bound in a Fixed Time", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.game_mode2.Select.start(manager);
            }
        });
        
        menu.addButton("Random Order", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.game_mode3.Select.start(manager);
            }
        });
        
        menu.addExitButton();
        manager.addWindow(menu);
    }
}
