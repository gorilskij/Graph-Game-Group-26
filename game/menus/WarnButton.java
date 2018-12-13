package game.menus;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.Timer;

import java.util.ArrayList;

public class WarnButton extends JButton {
    public static final int defaultTime = 1000;
    private static final ArrayList<WarnButton> buttons = new ArrayList<WarnButton>();
    
    private final String text;
    private final String warn;
    private final int time;
    private final ActionListener action;
    private Timer timer = null;
    
    public WarnButton(String tx, String wa, int ti, ActionListener ac) {
        super();
        text = tx;
        warn = wa;
        time = ti;
        action = ac;
        
        setText(text);
        
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (timer == null)
                    warn();
                else
                    action.actionPerformed(null);
            }
        });
        
        buttons.add(this);
    }
    
    private void warn() {
        setText(warn);
        
        timer = new Timer(time, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopWarn();
            }
        });
        
        timer.start();
        
        for (WarnButton button : buttons)
            if (button != this)
                button.stopWarn();
    }
    
    private void stopWarn() {
        if (timer == null)
            return;
        
        setText(text);
        timer.stop();
        timer = null;
    }
}