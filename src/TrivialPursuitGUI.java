/*
  Created by Taylor on 3/30/2017.
 */

import javax.swing.*;
import java.awt.*;

public class TrivialPursuitGUI {
    private static void createAndShowGUI() {

        JFrame frame = new JFrame("Trivial Pursuit");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(800, 600));

        JLabel label = new JLabel("Trivial Pursuit");
        panel.add(label);

        frame.getContentPane().add(panel);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(TrivialPursuitGUI::createAndShowGUI);
    }
}