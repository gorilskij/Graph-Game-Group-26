package game.useful;

import game.graph.Node;
import game.graph.Edge;

import java.awt.Color;
import java.awt.Point;

public class Tools {
    // transform the range of a number from [f0, t0] to [f1, t1]
    public static double range(double n, double f0, double t0, double f1, double t1) {
        double r0 = t0 - f0;
        double r1 = t1 - f1;
        double scale = r1 / r0;
        double m = ((n - f0) * scale) + f1;
        return Double.isFinite(m) ? m : 0;
    }
    
    // random integer from "from" to "to", inclusive
    public static int randInt(int from, int to) {
        if (to < from) {
            System.err.println("error: invalid range (" + from + " - " + to + ")");
            System.exit(1);
        }

        return (int) (Math.random() * (to - from + 1) + from);
    }
    
    // Color -> hex
    private static int rgb2int(Color color) {
        return color.getRed() * 65_536 + color.getGreen() * 256 + color.getBlue();
    }
    
    // hex -> Color
    private static Color int2rgb(int h) {
        return new Color(h / 65_536, (h %= 65_536) / 256, h % 256);
    }
    
    // invert color on the color wheel, keep same brightness
    public static Color invertColor(Color color) {
        return int2rgb(0xffffff - rgb2int(color));
    }
	
    // make a color darker (for highlighting and game mode 3)
	public static Color darkenColor(Color color) {
        int factor = 3;
		return new Color(color.getRed() / factor, color.getGreen() / factor, color.getBlue() / factor);
	}
    
    // swap two elements of an array ad the given indices
    public static void swap(Object[] array, int i, int j) {
        Object hold = array[i];
        array[i] = array[j];
        array[j] = hold;
    }
    
    // display an integer number of seconds either as XXs or as X:XX (or XX:XX)
    public static String timeToString(int secs) {
        int mins = secs / 60;
        secs %= 60;
        if (mins > 0) return String.format("%d:%02d", mins, secs);
        else return secs + "s";
    }
    
    // these methods aren't currently used but might be useful in the future
    /*
    public static boolean between(Point.Double p, Point.Double a, Point.Double b) {
        double t = 0; // tolarance
        if (Math.min(a.x, b.x) - t <= p.x && Math.max(a.x, b.x) + t >= p.x && Math.min(a.y, b.y) - t <= p.y && Math.max(a.y, b.y) + t >= p.y)
            return true;

        return false;
    }

    public static boolean onALine(Point.Double p0, Point.Double p1, Point.Double p2, double tolerance) {
        // tests if 3 points are on a line
        double x0 = p0.x, y0 = p0.y,
            x1 = p1.x, y1 = p1.y,
            x2 = p2.x, y2 = p2.y;

        double d = Math.abs(x0 * (y1 - y2) + x1 * (y2 - y0) + x2 * (y0 - y1)) / Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
        // System.out.println("d: " + d);
        return d <= tolerance;
    }

    private static Point.Double intersectionPoint(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
        double x = ((x0 * y1 - y0 * x1) * (x2 - x3) - (x0 - x1) * (x2 * y3 - y2 * x3)) / ((x0 - x1) * (y2 - y3) - (y0 - y1) * (x2 - x3));
        double y = ((x0 * y1 - y0 * x1) * (y2 - y3) - (y0 - y1) * (x2 * y3 - y2 * x3)) / ((x0 - x1) * (y2 - y3) - (y0 - y1) * (x2 - x3));

        if (!(Double.isFinite(x) && Double.isFinite(y)))
            return null;

        return new Point.Double(x, y);
    }
    public static Point.Double intersectionPoint(Point.Double p0, Point.Double p1, Point.Double p2, Point.Double p3) {
        return intersectionPoint(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }

    public static boolean edgesIntersect(Point.Double p0, Point.Double p1, Point.Double p2, Point.Double p3) {
        var point = intersectionPoint(p0, p1, p2, p3);

        return point != null && between(point, p0, p1) && between(point, p2, p3);
    }
    public static boolean edgesIntersect(Node n0, Node n1, Node n2, Node n3) {
        return edgesIntersect(n0.point(), n1.point(), n2.point(), n3.point());
    }
    public static boolean edgesIntersect(Edge e0, Edge e1) {
        return edgesIntersect(e0.a, e0.b, e1.a, e1.b);
    }
    */
}