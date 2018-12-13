package game.menus;

import game.useful.Tools;

import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class SliderSet extends JPanel {
    private JSlider minNodeSlider;
    private JSlider maxNodeSlider;
    private JSlider minEdgeSlider;
    private JSlider maxEdgeSlider;
    private JLabel nodeLabel;
    private JLabel edgeLabel;
    
    private boolean time;
    private JSlider minTimeSlider;
    private JSlider maxTimeSlider;
    private JLabel timeLabel;
    
    public SliderSet() {this(false);}
    public SliderSet(boolean t) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        time = t;
        
        makeSliders();
        makeInteractions();
        
        add(new JLabel("Please choose how many nodes you wish to color..."));
        add(minNodeSlider);
        add(maxNodeSlider);
        addSpace(5);
        add(nodeLabel);
        
        addSep();
        
        add(new JLabel("And how many edges..."));
        add(minEdgeSlider);
        add(maxEdgeSlider);
        addSpace(5);
        add(edgeLabel);
        
        if (time) {
            addSep();
            
            add(new JLabel("And how much time you wish to have..."));
            add(minTimeSlider);
            add(maxTimeSlider);
            addSpace(5);
            add(timeLabel);
        }
    }
    
    @Override
    public Component add(Component comp) {
        var component = (JComponent) comp;
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        return super.add(component);
    }
    
    public int minNodes() {return minNodeSlider.getValue();}
    public int maxNodes() {return maxNodeSlider.getValue();}
    public int minEdges() {return minEdgeSlider.getValue();}
    public int maxEdges() {return maxEdgeSlider.getValue();}
    public int minTime() {return minTimeSlider.getValue();}
    public int maxTime() {return maxTimeSlider.getValue();}
    
    public int randNodes() {return Tools.randInt(minNodes(), maxNodes());}
    public int randEdges() {return Tools.randInt(minEdges(), maxEdges());}
    public int randTime() {return Tools.randInt(minTime(), maxTime());}
    
    private void sliderSetup(JSlider slider, int tickSpacing, int labelSpacing) {
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setMinorTickSpacing(tickSpacing);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(labelSpacing);
    }
    
    private void setLabelText(JLabel label, JSlider minSlider, JSlider maxSlider, String one, String many) {
        int minValue = minSlider.getValue();
        int maxValue = maxSlider.getValue();
        String text = maxValue == 1 ? one : many;
        label.setText((minValue == maxValue ? minValue : minValue + " - " + maxValue) + " " + text);
    }
    
    private void makeSliders() {
        minNodeSlider = new JSlider(JSlider.HORIZONTAL, 15, 50, 15);
        sliderSetup(minNodeSlider, 1, 5);
        maxNodeSlider = new JSlider(JSlider.HORIZONTAL, 15, 50, 30);
        sliderSetup(maxNodeSlider, 1, 5);
        minEdgeSlider = new JSlider(JSlider.HORIZONTAL, 30, 190, 30);
        sliderSetup(minEdgeSlider, 5, 30);
        maxEdgeSlider = new JSlider(JSlider.HORIZONTAL, 30, 190, 50);
        sliderSetup(maxEdgeSlider, 5, 30);
        if (time) {
            minTimeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
            sliderSetup(minTimeSlider, 1, 1);
            maxTimeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 10);
            sliderSetup(maxTimeSlider, 1, 1);
        }
        
        nodeLabel = new JLabel();
        edgeLabel = new JLabel();
        var nodeListener = new ChangeListener() {public void stateChanged(ChangeEvent e) {
            setLabelText(nodeLabel, minNodeSlider, maxNodeSlider, "node", "nodes");
        }};
        minNodeSlider.addChangeListener(nodeListener);
        maxNodeSlider.addChangeListener(nodeListener);
        nodeListener.stateChanged(null); // initialize label text
        var edgeListener = new ChangeListener() {public void stateChanged(ChangeEvent e) {
            setLabelText(edgeLabel, minEdgeSlider, maxEdgeSlider, "edge", "edges");
        }};
        minEdgeSlider.addChangeListener(edgeListener);
        maxEdgeSlider.addChangeListener(edgeListener);
        edgeListener.stateChanged(null); // initialize label text
        if (time) {
            timeLabel = new JLabel();
            var timeListener = new ChangeListener() {public void stateChanged(ChangeEvent e) {
                setLabelText(timeLabel, minTimeSlider, maxTimeSlider, "minute", "minutes");
            }};
            minTimeSlider.addChangeListener(timeListener);
            maxTimeSlider.addChangeListener(timeListener);
            timeListener.stateChanged(null); // initialize label text
        }
    }
    
    private void minMaxRelation(JSlider minSlider, JSlider maxSlider) {
        minSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = minSlider.getValue();
                if (value > maxSlider.getValue()) maxSlider.setValue(value);
            }
        });
        maxSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = maxSlider.getValue();
                if (value < minSlider.getValue()) minSlider.setValue(value);
            }
        });
    }
    
    private void makeInteractions() {
        minMaxRelation(minNodeSlider, maxNodeSlider);
        minMaxRelation(minEdgeSlider, maxEdgeSlider);
        if (time) minMaxRelation(minTimeSlider, maxTimeSlider);
        
        minNodeSlider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
            int value = minNodeSlider.getValue();
            int minValue = value - 1;
            int maxValue = value * (value - 1) / 2;
            if (minValue > minEdgeSlider.getValue()) minEdgeSlider.setValue(minValue);
            if (maxValue < minEdgeSlider.getValue()) minEdgeSlider.setValue(maxValue);
        }});
        maxNodeSlider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
            int value = maxNodeSlider.getValue();
            int minValue = value - 1;
            int maxValue = value * (value - 1) / 2;
            if (minValue > maxEdgeSlider.getValue()) maxEdgeSlider.setValue(minValue);
            if (maxValue < maxEdgeSlider.getValue()) maxEdgeSlider.setValue(maxValue);
        }});
        minEdgeSlider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
            int value = minEdgeSlider.getValue();
            int minValue = (int) Math.ceil((1 + Math.sqrt(8 * value + 1)) / 2);
            int maxValue = value + 1;
            
            if (minValue > minNodeSlider.getValue()) minNodeSlider.setValue(minValue);
            if (maxValue < minNodeSlider.getValue()) minNodeSlider.setValue(maxValue);
        }});
        maxEdgeSlider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
            int value = maxEdgeSlider.getValue();
            int minValue = (int) Math.ceil((1 + Math.sqrt(8 * value + 1)) / 2);
            int maxValue = value + 1;
            if (minValue > maxNodeSlider.getValue()) maxNodeSlider.setValue(minValue);
            if (maxValue < maxNodeSlider.getValue()) maxNodeSlider.setValue(maxValue);
        }});
    }
    
    private void addSpace(int vSpace) {
        var space = new JPanel();
        space.setPreferredSize(new Dimension(0, vSpace));
        add(space);
    }
    
    private void addSep() {
        addSpace(5);
        add(new JSeparator());
        addSpace(5);
    }
}