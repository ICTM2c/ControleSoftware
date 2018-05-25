package ControleSoftware.Components;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {
    private int _headerFontSize = 20;
    JLabel header;
    private JPanel _subPanel;

    public JPanel getSubPanel() {
        return _subPanel;
    }
    private void setFontSize(int size) {
        _headerFontSize = size;
        header.setFont(new Font("Serif", Font.BOLD, _headerFontSize));
    }

    public JPanel fillPanelWithComponents(Component... components) {
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//        subPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        subPanel.setBorder
        subPanel.setMinimumSize(new Dimension(0, 0));
        for (Component c : components) {
            if (c instanceof HeaderPanel) {
                ((HeaderPanel) c).setFontSize(_headerFontSize - 6);
            }
            subPanel.add(c);
        }

        return subPanel;
    }


    public HeaderPanel(String headerText, JButton btn, Component... components) {
//        setBorder(BorderFactory.createLineBorder(Color.RED));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        header = new JLabel(headerText);
        setFontSize(20);
        add(header);

        _subPanel = fillPanelWithComponents(components);
        add(_subPanel);
//        if (components.length != 0) {
//            add(subPanel);
//        }

        if (btn != null) {
            _subPanel.add(btn);
        }
    }
}
