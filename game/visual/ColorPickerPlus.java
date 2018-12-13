package game.visual;

import game.menus.WindowManager;
import game.menus.DoneMethods;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.Timer;

public class ColorPickerPlus extends ColorPicker {
    JButton minusButton;
    JButton plusButton;
    JLabel timeLabel;
    public final int timeGiven;
    
    public ColorPickerPlus(int nColors, JPanel cc, int seconds, WindowManager manager, int gameMode) {
        super(nColors, cc, manager);
        
        // remove existing buttons to put +/- in front
        for (JComponent c : actionComponents) if (c != null) buttonSubPanel.remove(c);
        
        timeGiven = seconds;
        timeLabel = new JLabel();
        
        var listener = new ActionListener() {public void actionPerformed(ActionEvent e) {
            int its = timer.iterations + 1; // starting at 0s, not 1
            String sign = "-";
            
            if (its > seconds) {
                if (gameMode == 2) {
                    DoneMethods.timeOut(manager, board, timeGiven);
                } else {
                    sign = "+";
                    timeLabel.setForeground(Color.RED);
                    its -= seconds;
                }
            } else its = seconds - its;
            
            if (its < 60) timeLabel.setText(sign + its + "s");
            else {
                int mins = its / 60;
                int secs = its % 60;
                timeLabel.setText(sign + String.format("%d:%02d", mins, secs));
            }
            
            timeLabel.setText(" " + timeLabel.getText());
        }};
        listener.actionPerformed(null);
        timer.addActionListener(listener);
        
        buttonSubPanel.add(timeLabel);
        
        // re-add removed buttons
        for (JComponent c : actionComponents) if (c != null) buttonSubPanel.add(c);
    }
    
    @Override
    public void giveBoard(Board b) {
        super.giveBoard(b);
        if (board.gameMode != 2) {
            minusButton = new JButton("-");
            minusButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
                removeColor();
            }});
            plusButton = new JButton("+");
            plusButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
                addColor();
            }});
            buttonSubPanel.add(plusButton);
            buttonSubPanel.add(minusButton);
        }
    }
    
    private void removeColor() {
        
        if (colors.length <= 1) {
            System.err.println("you shouldn't have gotten here");
            return;
        }
        
        board.removeColor(colors[colors.length - 1]);
        
        var newColors = new Color[colors.length - 1];
        for (int i = 0; i < colors.length - 1; i++)
            newColors[i] = colors[i];
        
        var newButtons = new ColorButton[buttons.length - 1];
        for (int i = 0; i < buttons.length - 1; i++)
            newButtons[i] = buttons[i];
        
        remove(buttons[buttons.length - 1]);
        
        colors = newColors;
        buttons = newButtons;
        
        revalidate(); // maybe useless
        repaint();
        
        updateButtons();
    }
    
    private void addColor() {
        if (colors.length >= ColorPrecedence.nColors()) {
            System.err.println("you shouldn't have gotten here 2");
            return;
        }
        
        var newColors = new Color[colors.length + 1];
        for (int i = 0; i < colors.length; i++)
            newColors[i] = colors[i];
        
        var newButtons = new ColorButton[buttons.length + 1];
        for (int i = 0; i < buttons.length; i++)
            newButtons[i] = buttons[i];
        
        var newColor = ColorPrecedence.colors[newColors.length - 1];
        newColors[newColors.length - 1] = newColor;
        colors = newColors;
        
        var newButton = ColorButton.getNew(newColor, this);
        newButtons[newButtons.length - 1] = newButton;
        buttons = newButtons;
        
        remove(buttonSubPanel);
        add(newButton);
        add(buttonSubPanel);
        
        revalidate(); // maybe useless
        repaint();
        
        updateButtons();
    }
    
    private void updateButtons() {
        boolean any = false;
        for (ColorButton cb : buttons) if (cb.isSelected()) {
            any = true;
            break;
        }
        
        if (!any) pickColor(buttons[buttons.length - 1].color);
        
        if (colors.length >= ColorPrecedence.nColors()) plusButton.setEnabled(false);
        else plusButton.setEnabled(true);
        
        if (colors.length == 1) minusButton.setEnabled(false);
        else minusButton.setEnabled(true);
    }
    
    // public void gameEnd() {
        // System.out.println("timed gameEnd called");
        // int timeTaken;
        // if (listener.inOvertime) timeTaken = listener.overtime + totalSeconds;
        // else timeTaken = listener.elapsed;
        // timer.stop();
        // REPLACE true WITH WIN/LOSE STATE!!! ####################
        // DoneWindow.start(true, totalSeconds, timeTaken, board.manager);
    // }
    
    // public int stopTimer() {
    //     int timeTaken;
    //     if (listener.inOvertime) timeTaken = listener.overtime + totalSeconds;
    //     else timeTaken = listener.elapsed;
    //     timer.stop();
    //     return timeTaken;
    // }
}