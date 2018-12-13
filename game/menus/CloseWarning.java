package game.menus;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

public class CloseWarning {
    public static void start(String text, WindowManager manager) {
        var window = new Selection("Warning", manager);
        
        window.addText(text, 25);
        
        var yes = new JButton("Yes");
        yes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.goBack(); // close this window
                manager.warningYes(); // perform pending action
            }
        });
        window.buttonPanel.add(yes);
        
        var no = new JButton("No");
        no.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manager.goBack(); // close this window
                manager.queue.last().enabled(); // cancel
            }
        });
        window.buttonPanel.add(no);
        
        manager.addWindow(window, false); // false is to overlay over last window
    }
}