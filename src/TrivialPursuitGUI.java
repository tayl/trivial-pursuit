import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Taylor on 4/6/2017.
 */

public class TrivialPursuitGUI extends JFrame {

    private GamePanel gamePanel;
    private GraphicAssets graphicAssets;
    private Dimension screen_resolution;
    private int applet_width;
    private int applet_height;


    public TrivialPursuitGUI() {
        // create a new GamePanel which will have all drawing done on it
        gamePanel = new GamePanel();

        // set focusable so keystrokes are visible to the listeners
        gamePanel.setFocusable(true);

        // double buffer to prevent dropped frames
        gamePanel.setDoubleBuffered(true);

        // start paint timer
        gamePanel.timer.start();

        // set applet dimensions to 70% of screen width, or 1920 pixels, whichever is less
        screen_resolution = Toolkit.getDefaultToolkit().getScreenSize();
        applet_width = (int) (screen_resolution.getWidth() * .7);
        if (applet_width > 1920) {
            applet_width = 1920;
        }
        applet_height = (int) (applet_width / (16D / 9D)) + 50;

        // initialize JFrame layout, set dimensions, set title, set background color, disable resizing, center, instruct jvm to shut down when exit initiated
        setLayout(new BorderLayout());
        setSize(applet_width, applet_height);
        setTitle("Trivial Pursuit");
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // add panel to JFrame
        add("Center", gamePanel);

        // set window visible
        setVisible(true);

        // GraphicAssets requires JPanel dimensions, must be initialized after setVisible is called
        graphicAssets = new GraphicAssets(gamePanel.getWidth());

    }

    public static void main(String[] s) {
        TrivialPursuitGUI trivialPursuitGUI = new TrivialPursuitGUI();
    }

    /**
     * All drawing done here.
     */
    private class GamePanel extends JPanel implements ActionListener {

        // paint timer
        private final Timer timer = new Timer(50, this);
        // flag representing status of menu, is set when escape is pressed
        private boolean menu;
        // flag representing player name entry dialog, is set when open
        private boolean name_entry_dialog;
        private char[] valid_text_buffer;
        private int valid_text_buffer_ptr;
        // the active game
        private Game game;
        // players in the active game
        private Player[] players;
        // placeholder image object, not used for any one thing
        private Image image = null;

        public GamePanel() {
            valid_text_buffer = new char[1024];

            // testing code
            players = new Player[5];
            players[0] = new Player("Charlie");
            players[0].human = true;
            players[0].setWedge(Category.ARTS);
            players[0].setWedge(Category.SCIENCE);
            players[0].setWedge(Category.SPORTS);
            players[0].setWedge(Category.PLACES);
            players[0].setWedge(Category.EVENTS);
            players[0].setWedge(Category.ENTERTAINMENT);
            players[1] = new Player("Linus");
            players[1].setWedge(Category.PLACES);
            players[1].setWedge(Category.EVENTS);
            players[1].setWedge(Category.ENTERTAINMENT);
            players[2] = new Player("Snoopy");
            players[2].setWedge(Category.SPORTS);
            players[2].setWedge(Category.PLACES);
            players[3] = new Player("Lucy");
            players[3].setWedge(Category.EVENTS);
            players[3].setWedge(Category.ENTERTAINMENT);
            players[4] = new Player("Violet");
            players[4].setWedge(Category.ARTS);
            players[4].setWedge(Category.SCIENCE);

            try {
                game = new Game(players);
            } catch (IOException e) {
                e.printStackTrace();
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println("Mouse pressed at " + e.getX() + ", " + e.getY());

                    // logic here to translate coordinates to game piece presses
                    // polar coords?

                    // handle key presses in menu, only two buttons, easy to hard map
                    if (menu) {

                    }

                    super.mousePressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    System.out.println("Mouse released at " + e.getX() + ", " + e.getY());
                    super.mouseReleased(e);
                }
            });

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    System.out.println("Key typed " + e.getKeyCode());
                    super.keyTyped(e);
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // escape key pressed, trigger menu
                    if (e.getKeyCode() == 27) {
                        menu = !menu;
                    }

                    // capture player names when name dialog appears
                    // accept on enter or mouse click
                    if (name_entry_dialog && ((e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z') || e.getKeyChar() == 10)) {
                        valid_text_buffer[valid_text_buffer_ptr++ % valid_text_buffer.length] = e.getKeyChar();
                        System.out.println(Arrays.toString(valid_text_buffer));
                    }

                    System.out.println("Key pressed " + e.getKeyCode());
                    super.keyPressed(e);
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    System.out.println("Key released " + e.getKeyCode());
                    super.keyReleased(e);
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(Color.black);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.white);

            if (!graphicAssets.isLoaded()) {
                Random random = new Random();

                image = graphicAssets.getImage("menu_Screen_Landscape.png");
                if (image != null) {
                    g.drawImage(image, 0, (getHeight() - image.getHeight(null)) / 2, this);
                }

                for (int x = 0; x < getWidth(); x += 30) {
                    for (int y = 0; y < getHeight(); y += 30) {
                        g.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.05f));
                        g.fillRect(x, y, 30, 30);
                    }
                }
                g.setColor(Color.white);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                g.drawString("graphicAssets loading", (int) (getWidth() * .1), getHeight() / 2);
                g.drawRect((int) (getWidth() * .1), getHeight() / 2, (int) (getWidth() * .8), graphicAssets.scaledCoordinate(50));
                g.fillRect((int) (getWidth() * .1), getHeight() / 2, (int) (getWidth() * .8 * graphicAssets.getProgress()), graphicAssets.scaledCoordinate(50));
                return;
            }

            image = graphicAssets.getImage("gameBoard.png");
            g.drawImage(image, 0, (getHeight() - image.getHeight(null)) / 2, this);

            drawPlayerCards(g);

            // menu must remain at the bottom
            if (menu) {
                drawMenu(g);
            }
        }

        private void drawMenu(Graphics g) {
            g.setColor(new Color(0f, 0f, 0f, 0.5f));
            g.fillRect(0, 0, getWidth(), getHeight());
            image = graphicAssets.getImage("btn_new.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) - image.getWidth(null) / 8, getHeight() / 2, null);
            image = graphicAssets.getImage("btn_cont.png");
            g.drawImage(image, getWidth() / 2 + image.getWidth(null) / 8, getHeight() / 2, null);
        }

        private void drawPlayerCards(Graphics g) {
            int x = 20;
            int y = getHeight() / 2 - graphicAssets.scaledCoordinate(144) * players.length / 2;


            for (int i = 0; i < players.length; i++) {

                image = graphicAssets.getImage("empty_Player_Card.png");

                // draw the base blank player card
                g.drawImage(image, x, y, this);

                // set the font and color
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.setColor(Color.black);

                // draw the players name
                g.drawString(players[i].getPlayerName(), x + graphicAssets.scaledCoordinate(152), y + graphicAssets.scaledCoordinate(42));

                // draw the players icon
                switch (i % 6) {
                    case 0:
                        image = graphicAssets.getImage("baby_Shia.png");
                        break;
                    case 1:
                        image = graphicAssets.getImage("even_Stevens_Shia.png");
                        break;
                    case 2:
                        image = graphicAssets.getImage("just_Do_It_Shia.png");
                        break;
                    case 3:
                        image = graphicAssets.getImage("mid_Twenties_Shia.png");
                        break;
                    case 4:
                        image = graphicAssets.getImage("not_Famous_Shia.png");
                        break;
                    case 5:
                        image = graphicAssets.getImage("well_Adjusted_Shia.png");
                        break;
                }

                g.drawImage(image, x + graphicAssets.scaledCoordinate(33), y + graphicAssets.scaledCoordinate(10), null);

                boolean[] wedges = players[i].getWedges();

                if (wedges[0]) {
                    image = graphicAssets.getImage("yellow_Wedge.png");
                    g.drawImage(image, x + graphicAssets.scaledCoordinate(148), y + graphicAssets.scaledCoordinate(53), null);
                }

                if (wedges[1]) {
                    image = graphicAssets.getImage("pink_Wedge.png");
                    g.drawImage(image, x + graphicAssets.scaledCoordinate(167), y + graphicAssets.scaledCoordinate(53), null);
                }

                if (wedges[2]) {
                    image = graphicAssets.getImage("green_Wedge.png");
                    g.drawImage(image, x + graphicAssets.scaledCoordinate(186), y + graphicAssets.scaledCoordinate(53), null);
                }

                if (wedges[3]) {
                    image = graphicAssets.getImage("blue_Wedge.png");
                    g.drawImage(image, x + graphicAssets.scaledCoordinate(205), y + graphicAssets.scaledCoordinate(53), null);
                }

                if (wedges[4]) {
                    image = graphicAssets.getImage("orange_Wedge.png");
                    g.drawImage(image, x + graphicAssets.scaledCoordinate(224), y + graphicAssets.scaledCoordinate(53), null);
                }

                if (wedges[5]) {
                    image = graphicAssets.getImage("purple_Wedge.png");
                    g.drawImage(image, x + graphicAssets.scaledCoordinate(243), y + graphicAssets.scaledCoordinate(53), null);
                }

                // offset the y coordinate for the next player card
                y += graphicAssets.scaledCoordinate(144);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.repaint();
        }
    }
}