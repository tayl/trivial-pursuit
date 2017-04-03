/**
 * Created by Taylor on 4/3/2017.
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TrivialPursuit extends JDialog {

    public TrivialPursuit(JFrame frame) {
        super(frame, "Trivial Pursuit", true);

        Dimension screen_resolution = Toolkit.getDefaultToolkit().getScreenSize();

        int scaled_width = (int) (screen_resolution.getWidth() * .8);
        int scaled_height = (int) (scaled_width / (16D / 9D));

        ImageIcon game_board_image = null;
        try {
            BufferedImage image = ImageIO.read(getClass().getResource("gameBoard.png"));
            game_board_image = new ImageIcon(image.getScaledInstance(scaled_width, scaled_height, 5));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        JPanel main = new JPanel(new BorderLayout());

        main.setBackground(Color.BLACK);
        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel game_board = new JLabel(game_board_image);

        game_board.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getX() + "," + e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println(e.getX() + "," + e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println(e.getX() + "," + e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                System.out.println(e.getX() + "," + e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                System.out.println(e.getX() + "," + e.getY());
            }
        });

        main.add(game_board, BorderLayout.CENTER);

        add(main);

        pack();
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Menu");
        JPanel menu = new JPanel();
        menu.add(new JButton(new AbstractAction("Play") {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrivialPursuit trivialPursuit = new TrivialPursuit(frame);
                trivialPursuit.setLocationRelativeTo(frame);
                trivialPursuit.setResizable(false);
                trivialPursuit.setVisible(true);
            }
        }));
        frame.add(menu);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
