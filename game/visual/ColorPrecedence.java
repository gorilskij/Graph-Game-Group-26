package game.visual;

import game.useful.Tools;

import java.awt.Color;

// temporary
import java.awt.*;
import javax.swing.*;

public class ColorPrecedence {
    public static void main(String[] args) {
        fillIn();
        
        var f = new JFrame();
        f.setLocationRelativeTo(null);
        var mp = new JPanel();
        mp.setLayout(new BoxLayout(mp, BoxLayout.Y_AXIS));
        for (Color color : colors) {
            var p = new JPanel();
            p.setBackground(color);
            p.setPreferredSize(new Dimension(300, 50));
            mp.add(p);
        }
        f.setContentPane(mp);
        f.pack();
        f.setVisible(true);
    }
    
    public static Color[] colors = {
        new Color(243, 28, 28), null, // red
        new Color(12, 20, 225), null, // blue
        new Color(16, 159, 25), null, // green
        new Color(0, 112, 225), null, // light blue
        new Color(153, 255, 153), null, // mint
        new Color(148, 89, 0), null, // brown
        new Color(250, 128, 114), null, // salmon
        new Color(191, 159, 114), null, // beige
        new Color(0, 255, 29), null, // lime
        new Color(181, 45, 45), null, // dark red
        new Color(88, 145, 88), null // cactus
    };
    public static void fillIn() {
        // run at startup
        for (int i = 0; i < colors.length - 1; i += 2) colors[i + 1] = Tools.invertColor(colors[i]);
        var newColors = new Color[60];
        int index = 0;
        for (; index < colors.length; index++) newColors[index] = colors[index];
        for (; index < newColors.length; index++) {
            Color c = null;
            wh: while (true) {
                var cc = new Color(Tools.randInt(0,255), Tools.randInt(0,255), Tools.randInt(0,255));
                for (Color ccc : colors) if (cc.equals(ccc)) continue wh;
                c = cc;
                break;
            }
            newColors[index] = c;
        }
        colors = newColors;
    }
    
    public static int nColors() {
        return colors.length;
    }
    
    public static int numberOf(Color color) {
        for (int i = 0; i < colors.length; i++)
            if (color.equals(colors[i]))
                return i;
        
        System.err.println("error: you shouldn't have gotten here");
        System.exit(1);
        return -1; // formality
    }
}