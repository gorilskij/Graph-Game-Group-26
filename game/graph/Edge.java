package game.graph;

import game.useful.Tools;
import game.graph.basic.BasicEdge;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class Edge extends BasicEdge<Node> {
    public static final Color baseColor = Color.WHITE;
    public Color color = baseColor;
    private static final double normalThickness = 0.003;
    private static final double highlightThickness = 0.005;
    
    // styling
    public static final int NORMAL = 10; // +10 to avoid interference with Node.style
    public static final int THICK = 1;
    public static final int DARK = 2;
    
    public int style = NORMAL;
    
    public boolean gm3 = false;
    public void draw(Graphics g, Dimension size, int border) {
        int width = (int) size.getWidth() - 2 * border;
        int height = (int) size.getHeight() - 2 * border;
        int average = (width + height) / 2;
        double thickness = style == THICK ? highlightThickness : normalThickness;
        
        g.setColor(style == DARK || style != THICK && gm3 ? Tools.darkenColor(color) : color);
        var g2D = (Graphics2D) g;
        g2D.setStroke(new BasicStroke((int) (average * thickness)));
        
        g2D.drawLine(
            (int) (width * a.x) + border,
            (int) (height * a.y) + border,
            (int) (width * b.x) + border,
            (int) (height * b.y) + border
        );
    }
}