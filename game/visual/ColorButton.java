package game.visual;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.UIManager;

public class ColorButton extends JButton {
    public int width;
    public int height;
    public Color color;
    private ColorPicker parent;
    
    private boolean selected = false;
    
    private Border unselectedBorder;
    private Border selectedBorder;
    
    // used instead of constructor to allow for ui to be changed before calling super()
    // nimbus buttons look bad
    public static ColorButton getNew(Color c, ColorPicker p) {
        return new ColorButton(c, p);
        
        // doesn't look nice on windows
        // var myLF = UIManager.getLookAndFeel();
        // ColorButton button = null;
        // try {
        //     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //     button = new ColorButton(c, p);
        //     UIManager.setLookAndFeel(myLF);
        // } catch (Exception e) {
        //     System.err.println("this is bad");
        //     System.exit(1);
        // }
        // return button;
    }
    
    // outside code should use getNew, not the constructor
    private ColorButton(Color cc, ColorPicker pp) {
        super();
        
        color = cc;
        parent = pp;
        
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.pickColor(color);
            }
        });
        
        setBackground(color);
        setOpaque(true);
        
        unselectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 12, 0, 0),
            BorderFactory.createLineBorder(color, 12)
        );
        
        selectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 0),
            BorderFactory.createLineBorder(color, 20)
        );
        
        setBorder(unselectedBorder);
    }
    
    public boolean isSelected() {return selected;}
    
    public void select() {
        if (!selected) {
            setBorder(selectedBorder);
            selected = true;
        }
    }
    
    public void deselect() {
        if (selected) {
            setBorder(unselectedBorder);
            selected = false;
        }
    }
}