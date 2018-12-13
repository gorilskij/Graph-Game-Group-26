package game.visual;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class ColorTimer extends Timer {
    public int iterations = 0;
    public ColorTimer(int interval) {
        super(interval, null);
        addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {iterations++;}});
    }
}