import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Taylor on 4/6/2017.
 */

public class TrivialPursuitGUI extends JFrame {

    public GraphicAssets graphicAssets;
    public GamePanel gamePanel;


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
        Dimension screen_resolution = Toolkit.getDefaultToolkit().getScreenSize();
        int applet_width = (int) (screen_resolution.getWidth() * .7);
        if (applet_width > 1920) {
            applet_width = 1920;
        }
        int applet_height = (int) (applet_width / (16D / 9D)) + 50;

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
        TrivialPursuitGUI gui = new TrivialPursuitGUI();
    }

    /**
     * All drawing done here.
     */
    private class GamePanel extends JPanel implements ActionListener {

        // paint timer
        private final Timer timer = new Timer(50, this);
        // map of player space (index) to coordinate pair (x pixels, y pixels)
        // in 1920 / 1080 resolution
        Point[] positionMap;
        // variable used for storing nanotime
        private double timeVar;
        // menu flags - when set, menu is being drawn
        private boolean start_menu = true;
        private boolean playing_number_selection_menu;
        private boolean name_entry_menu;
        private boolean game_piece_menu;
        // the current player being configured in the players array
        private int current_player_setup;
        // flags used for the fade transition
        private boolean fading;
        private boolean transition;
        private int fade_count;
        // are we playing?
        private boolean playing;
        // text buffer and pointers to accept user names
        private char[] valid_text_buffer;
        private int valid_text_buffer_end;
        private int valid_text_buffer_start;
        // the active game
        private Game game;
        // players in the active game
        private Player[] players;
        // placeholder image object, not used for any one thing
        private Image image = null;

        private GamePanel() {
            valid_text_buffer = new char[32];

            // try to read the PositionMap.txt file into the point array
            positionMap = new Point[73];
            try {
                Scanner in = new Scanner(new File("positionMap.txt"));
                for (int i = 0; i < 73; i++) {
                    int x = in.nextInt();
                    int y = in.nextInt();
                    // the first int per line is the x value, 2nd int is the y value
                    positionMap[i] = new Point(x, y);
                }

                // close it when we're done
                in.close();
            } catch (IOException e) {
                System.out.println("Couldn't open PositionMap.txt");
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    System.out.println("Mouse pressed at " + e.getX() + ", " + e.getY());

                    // logic here to translate coordinates to game piece presses
                    // polar coords?

                    //TODO
//                    if (game.isAwaitingAnswerChoice()) {
//                        if (boundsContainCoords(answer_x, answer_y, image, e)) {
//                            game.setAnswerChoice(answer);
//                        }
//                    }
//
//                    if (game.isAwaitingStumpChoice()) {
//                        if (boundsContainCoords(stump_x, stump_y, image, e)) {
//                            game.setStumpChoice(answer);
//                        }
//                    }

                    // this logic handles clicks for the game piece selection menu. it checks whether the mouse click coordinates fall over a game piece image
                    // if they do, the current user being configured is assigned that game piece
                    if (game_piece_menu && !name_entry_menu) {
                        // all pieces have the same dimensions, so only load one
                        image = graphicAssets.getImage("baby_Shia.png");
                        if (boundsContainCoords(getWidth() / 2 - image.getWidth(null) / 2 * 4, getHeight() / 2 - image.getHeight(null) / 2, image, e)) {
                            players[current_player_setup].setGamePiece(0);
                            System.out.println("Player " + current_player_setup + " given piece 0");
                            current_player_setup++;
                            fading = true;
                        }

                        if (boundsContainCoords(getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 - image.getHeight(null) / 2, image, e)) {
                            players[current_player_setup].setGamePiece(1);
                            System.out.println("Player " + current_player_setup + " given piece 1");
                            current_player_setup++;
                            fading = true;
                        }

                        if (boundsContainCoords(getWidth() / 2 + image.getWidth(null) / 2 * 2, getHeight() / 2 - image.getHeight(null) / 2, image, e)) {
                            players[current_player_setup].setGamePiece(2);
                            System.out.println("Player " + current_player_setup + " given piece 2");
                            current_player_setup++;
                            fading = true;
                        }

                        if (boundsContainCoords(getWidth() / 2 - image.getWidth(null) / 2 * 4, getHeight() / 2 + image.getHeight(null) / 2 * 2, image, e)) {
                            players[current_player_setup].setGamePiece(3);
                            System.out.println("Player " + current_player_setup + " given piece 3");
                            current_player_setup++;
                            fading = true;
                        }

                        if (boundsContainCoords(getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 + image.getHeight(null) / 2 * 2, image, e)) {
                            players[current_player_setup].setGamePiece(4);
                            System.out.println("Player " + current_player_setup + " given piece 4");
                            current_player_setup++;
                            fading = true;
                        }

                        if (boundsContainCoords(getWidth() / 2 + image.getWidth(null) / 2 * 2, getHeight() / 2 + image.getHeight(null) / 2 * 2, image, e)) {
                            players[current_player_setup].setGamePiece(5);
                            System.out.println("Player " + current_player_setup + " given piece 5");
                            current_player_setup++;
                            fading = true;
                        }

                        // if we're in this menu but we have configured all the players, transition to playing state
                        if (current_player_setup >= players.length) {
                            playing = true;
                            fading = true;
                        }
                    }

                    // this logic handles clicks on the number of players selection menu, and initializes the players array with n players
                    // also transitions to the game piece selection and name selection
                    if (playing_number_selection_menu) {
                        image = graphicAssets.getImage("2_player.png");
                        if (boundsContainCoords(getWidth() / 2 - image.getWidth(null) / 2 * 4, (getHeight() - image.getHeight(null)) / 2, image, e)) {
                            players = new Player[2];
                            name_entry_menu = true;
                            game_piece_menu = true;
                            fading = true;
                            return;
                        }

                        if (boundsContainCoords(getWidth() / 2 - image.getWidth(null) / 2, (getHeight() - image.getHeight(null)) / 2, image, e)) {
                            players = new Player[3];
                            name_entry_menu = true;
                            game_piece_menu = true;
                            fading = true;
                            return;
                        }

                        if (boundsContainCoords(getWidth() / 2 + image.getWidth(null) / 2 * 2, (getHeight() - image.getHeight(null)) / 2, image, e)) {
                            players = new Player[4];
                            name_entry_menu = true;
                            game_piece_menu = true;
                            fading = true;
                            return;
                        }

                        if (boundsContainCoords(getWidth() / 2 - image.getWidth(null) / 3 * 4, (getHeight() / 2 + image.getHeight(null) / 3 * 2), image, e)) {
                            players = new Player[5];
                            name_entry_menu = true;
                            game_piece_menu = true;
                            fading = true;
                            return;
                        }

                        if (boundsContainCoords(getWidth() / 2 + image.getWidth(null) / 3, (getHeight() / 2 + image.getHeight(null) / 3 * 2), image, e)) {
                            players = new Player[6];
                            name_entry_menu = true;
                            game_piece_menu = true;
                            fading = true;
                            return;
                        }
                    }

                    // this logic handles clicks when the start menu is being drawn. only one button, "new game"
                    if (start_menu) {
                        image = graphicAssets.getImage("btn_new.png");
                        if (boundsContainCoords((getWidth() - image.getWidth(null)) / 2, (getHeight() - image.getHeight(null)) / 2, image, e)) {
                            playing_number_selection_menu = true;
                            fading = true;
                        }
                    }

                    super.mousePressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                }
            });

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                }

                @Override
                public void keyPressed(KeyEvent e) {

                    if (playing && game.isRollTheDie()) {
                        if (e.getKeyChar() == ' ') {
                            Die.rolled = true;
                        }
                    }

                    //TODO
                    if (playing && game.isAwaitingAnswerChoice() && (e.getKeyChar() >= '1' && e.getKeyChar() <= '4')) {
                        if (e.getKeyChar() == '1') {
                            game.setAnswerChoice(0);
                        }
                        if (e.getKeyChar() == '2') {
                            game.setAnswerChoice(1);
                        }
                        if (e.getKeyChar() == '3') {
                            game.setAnswerChoice(2);
                        }
                        if (e.getKeyChar() == '4') {
                            game.setAnswerChoice(3);
                        }
                    }

                    if (playing && game.isAwaitingStumpChoice() && (e.getKeyChar() >= '1' && e.getKeyChar() <= '2')) {
                        game.setStumpChoice(e.getKeyChar() == '2');
                    }

                    // escape key pressed, restart game
                    if (e.getKeyCode() == 27) {
                        start_menu = true;
                        playing_number_selection_menu = false;
                        name_entry_menu = false;
                        game_piece_menu = false;
                        current_player_setup = 0;
                        transition = false;
                        playing = false;
                        fading = true;
                    }

                    // backspace key, steps backwards in the text buffer to effectively backspace text
                    if (e.getKeyCode() == 8) {
                        if (valid_text_buffer_end > valid_text_buffer_start) {
                            valid_text_buffer_end--;
                        }
                    }

                    // if the name entry dialog is open and enter key is pressed, close it and accept the string as a player name
                    if (name_entry_menu && e.getKeyCode() == 10) {
                        name_entry_menu = false;
                        players[current_player_setup] = new Player(getBufferLastString());
                        players[current_player_setup].human = true;
                        valid_text_buffer_start = valid_text_buffer_end;
                        System.out.println("Set player " + current_player_setup + " name to " + players[current_player_setup].getPlayerName());
                    }

                    // capture text when name dialog appears and store to the buffer
                    // accept on enter or mouse click (TODO)
                    if (name_entry_menu && ((e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z') || e.getKeyChar() == ' ' || e.getKeyChar() == '!' || e.getKeyChar() == 10)) {
                        valid_text_buffer[valid_text_buffer_end++ % valid_text_buffer.length] = e.getKeyChar();
                        System.out.println(getBufferLastString());
                    }

                    super.keyPressed(e);
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    super.keyReleased(e);
                }
            });
        }

        @Override
        public void paint(Graphics g1d) {
            // convert the passed object to a 2d Graphics object (allows for text antialiasing among other things)
            Graphics2D g = (Graphics2D) g1d;
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // if the graphic assets are not yet loaded, draw the loading screen and stop painting on this tick
            if (!graphicAssets.isLoaded()) {
                drawLoadingScreen(g);
                drawLoadingBar(g);
                return;
            }

            // draws the player cards and game board
            if (playing) {
                drawGameBoard(g);
                drawPlayerCards(g);
                drawGameState(g);

                if (game != null) {
                    drawDie(g);
                }
            }

            // transitions from user configuration to playing state
            if (transition && playing && game_piece_menu && name_entry_menu) {
                game_piece_menu = false;
                name_entry_menu = false;
                // setup complete, start the game
                System.out.println("Setup complete, start the game");
                try {
                    game = new Game(players);
                    new Thread(game).start();
                    timeVar = System.nanoTime();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // transition between user configuration menus
            if (transition && game_piece_menu) {
                name_entry_menu = true;
            }

            // draws the game piece selection menu, as well as name entry if necessary
            if (game_piece_menu) {
                drawGamePieceSelectionMenu(g);
                if (name_entry_menu) {
                    drawShadowOverlay(g);
                    drawPlayerNameSelectionMenu(g, current_player_setup);
                }
            }

            // transition between game piece selection and player number selection menu
            if (transition && game_piece_menu && playing_number_selection_menu) {
                playing_number_selection_menu = false;
            }

            // draws the player number selection menu
            if (playing_number_selection_menu) {
                drawPlayerSelectionMenu(g);
            }

            // transition between start menu and playing number selection
            if (transition && playing_number_selection_menu && start_menu) {
                start_menu = false;
            }

            // draws the start menu
            if (start_menu) {
                drawLoadingScreen(g);
                drawStartMenu(g);
            }

            // handles fading
            if (fading) {
                drawFadingShade(g);
            }
        }

        /**
         * Given points to define a bounding box and a mouse event, this method will determine whether the mouse event
         * occurred within the bounds of the box.
         *
         * @param x     top left x coordinate of bounding box
         * @param y     top left y coordinate of bounding box
         * @param image image whose dimensions will be used to complete the bounding box with respect to x,y
         * @param e     mouse event whose coordinates will be checked for inclusion in the bounds
         * @return true if the mouse event took place within the bounds
         */
        private boolean boundsContainCoords(int x, int y, Image image, MouseEvent e) {
            int bottom_right_x = x + image.getWidth(null);
            int bottom_right_y = y + image.getHeight(null);

            return e.getX() > x && e.getX() < bottom_right_x && e.getY() > y && e.getY() < bottom_right_y;
        }

        /**
         * Returns the last string entered into the user text buffer, as determined by characters typed since enter was last pressed
         *
         * @return the last buffer string
         */
        private String getBufferLastString() {
            StringBuilder sb = new StringBuilder();
            for (int i = valid_text_buffer_start; i < valid_text_buffer_end; i++) {
                sb.append(valid_text_buffer[i % valid_text_buffer.length]);
            }
            return sb.toString();
        }

        /**
         * This method draws a curtain over the currently drawn scenes by temporarily replacing a rectangles alpha
         * channel with the sine function.
         * <p>
         * divisions member variable determines how quickly the curtain is drawn
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawFadingShade(Graphics2D g) {
            int divisions = 25;

            if (fading && fade_count == 0) {
                fade_count = divisions;
            }

            int difference = divisions - fade_count--;

            double portion_of_pi = ((double) difference / (double) divisions) * Math.PI;

            double number_between_0_and_1_and_then_0 = Math.sin(portion_of_pi);

            if (1 - number_between_0_and_1_and_then_0 < .01) {
                transition = true;
                System.out.println("DARK SCREEN");
            } else {
                transition = false;
            }

            drawShadowOverlay(g, (float) (number_between_0_and_1_and_then_0));

            if (fade_count == 0) {
                fading = false;
                transition = false;
            }
        }

        /**
         * Draws the game piece selection menu
         * <p>
         * https://i.imgur.com/f8Z9JfV.png
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawGamePieceSelectionMenu(Graphics2D g) {
            image = graphicAssets.getImage("game_Piece_Select_Landscape.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 - image.getHeight(null) / 2, null);

            image = graphicAssets.getImage("baby_Shia.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2 * 4, getHeight() / 2 - image.getHeight(null) / 2, null);

            image = graphicAssets.getImage("even_Stevens_Shia.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 - image.getHeight(null) / 2, null);

            image = graphicAssets.getImage("just_Do_It_Shia.png");
            g.drawImage(image, getWidth() / 2 + image.getWidth(null) / 2 * 2, getHeight() / 2 - image.getHeight(null) / 2, null);

            image = graphicAssets.getImage("mid_Twenties_Shia.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2 * 4, getHeight() / 2 + image.getHeight(null) / 2 * 2, null);

            image = graphicAssets.getImage("not_Famous_Shia.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 + image.getHeight(null) / 2 * 2, null);

            image = graphicAssets.getImage("well_Adjusted_Shia.png");
            g.drawImage(image, getWidth() / 2 + image.getWidth(null) / 2 * 2, getHeight() / 2 + image.getHeight(null) / 2 * 2, null);
        }

        // pauses for seconds number of seconds
        private void pause(double seconds) {
            timeVar = (double) System.nanoTime() / 1000000000.0;
            while (timeVar + seconds > (double) System.nanoTime() / 1000000000.0) {
            }
        }

        private void drawDie(Graphics2D g) {
            if (!Die.rolled) {
                image = graphicAssets.getImage("roll_Dice.png");
                g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 - image.getHeight(null) / 2, null);
                timeVar = (double) System.nanoTime() / 1000000000.0;
                return;
            }
            if (game.isRollTheDie()) {
                image = graphicAssets.getImage("dice_Drawing.png");
                g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 - image.getHeight(null) / 2, null);
                if ((double) System.nanoTime() / 1000000000.0 >= timeVar + 1.2) {
                    game.setRollTheDie(false);
                    timeVar = (double) System.nanoTime() / 1000000000.0;
                }
            } else if ((double) System.nanoTime() / 1000000000.0 <= timeVar + 2.0) {
                image = graphicAssets.getImage("roll_Dice_" + game.getDieRoll() + ".png");
                g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 - image.getHeight(null) / 2, null);
            }
        }

        /**
         * Draws the player name selection/input menu
         * <p>
         * https://i.imgur.com/Op5ukFe.png
         *
         * @param g      the Graphics2D/Graphics object to draw on
         * @param player the current player being configured (0-5)
         */
        private void drawPlayerNameSelectionMenu(Graphics2D g, int player) {
            image = graphicAssets.getImage("name_Select_Popup_R1.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 - image.getHeight(null) / 2, null);
            image = graphicAssets.getImage((player + 1) + "_player.png");
            g.drawImage(image, graphicAssets.scaledCoordinate(498), graphicAssets.scaledCoordinate(312), null);
            image = graphicAssets.getImage("n_player_overlay.png");
            g.drawImage(image, graphicAssets.scaledCoordinate(498), graphicAssets.scaledCoordinate(312), null);

            g.setColor(Color.white);
            g.fill3DRect(getWidth() / 2 - graphicAssets.scaledCoordinate(700) / 2, getHeight() / 2, graphicAssets.scaledCoordinate(700), graphicAssets.scaledCoordinate(100), true);
            g.setColor(Color.black);
            g.setFont(new Font("Calibri", Font.BOLD, 30));
            g.drawString(getBufferLastString(), getWidth() / 2 - graphicAssets.scaledCoordinate(330), getHeight() / 2 + graphicAssets.scaledCoordinate(70));
        }

        /**
         * Draws the number of players selection menu
         * <p>
         * https://i.imgur.com/KAI3Aud.png
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawPlayerSelectionMenu(Graphics2D g) {
            image = graphicAssets.getImage("player_Select_Screen_Landscape.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, (getHeight() - image.getHeight(null)) / 2, this);

            image = graphicAssets.getImage("2_player.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2 * 4, (getHeight() - image.getHeight(null)) / 2, this);

            image = graphicAssets.getImage("3_player.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, (getHeight() - image.getHeight(null)) / 2, this);

            image = graphicAssets.getImage("4_player.png");
            g.drawImage(image, getWidth() / 2 + image.getWidth(null) / 2 * 2, (getHeight() - image.getHeight(null)) / 2, this);

            image = graphicAssets.getImage("5_player.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 3 * 4, (getHeight() / 2 + image.getHeight(null) / 3 * 2), this);

            image = graphicAssets.getImage("6_player.png");
            g.drawImage(image, getWidth() / 2 + image.getWidth(null) / 3, (getHeight() / 2 + image.getHeight(null) / 3 * 2), this);
        }

        /**
         * Draws the start menu with "new game" button
         * <p>
         * https://i.imgur.com/AyknOFH.png
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawStartMenu(Graphics2D g) {
            image = graphicAssets.getImage("btn_new.png");
            g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, (getHeight() - image.getHeight(null)) / 2, this);
        }

        /**
         * Draws the game board graphic on top of a black background
         * <p>
         * https://i.imgur.com/871zkER.png
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawGameBoard(Graphics2D g) {
            g.setColor(Color.black);
            g.fillRect(0, 0, getWidth(), getHeight());
            image = graphicAssets.getImage("gameBoard.png");
            g.drawImage(image, 0, (getHeight() - image.getHeight(null)) / 2, this);
        }

        private void drawGameState(Graphics2D g) {
            Point point;
            if (game != null) {
                for (Player p : game.getPlayers()) {
                    point = positionMap[p.position];
                    g.drawImage(p.playerImage, graphicAssets.scaledCoordinate((int) point.getX() - 33), graphicAssets.scaledCoordinate((int) point.getY() - 10), null);
                }
            } else {
                for (Player p : players) {
                    point = positionMap[p.position];
                    g.drawImage(p.playerImage, graphicAssets.scaledCoordinate((int) point.getX() - 33), graphicAssets.scaledCoordinate((int) point.getY() - 10), null);
                }
            }
        }

        /**
         * Draws the loading screen graphic
         * <p>
         * https://i.imgur.com/jT295AD.png
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawLoadingScreen(Graphics2D g) {
            image = graphicAssets.getImage("menu_Screen_Landscape.png");
            if (image != null) {
                g.drawImage(image, 0, (getHeight() - image.getHeight(null)) / 2, this);
            }
        }

        /**
         * Draws the animated loading bar graphic
         * <p>
         * https://i.imgur.com/jT295AD.png
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawLoadingBar(Graphics2D g) {
            g.setColor(Color.white);
//            g.setFont(new Font("Calibri", Font.BOLD, 50));
//            g.drawString("loading graphics...", (int) (getWidth() * .1), getHeight() / 2);

            g.drawRect((int) (getWidth() / 2 - (getWidth() * .4) / 2), getHeight() / 2, (int) (getWidth() * .4), graphicAssets.scaledCoordinate(50));
            g.fillRect((int) (getWidth() / 2 - (getWidth() * .4) / 2), getHeight() / 2, (int) (getWidth() * .4 * graphicAssets.getProgress()), graphicAssets.scaledCoordinate(50));
        }

        /**
         * Draws a veil over the current state to bring attention to something drawn over itself
         *
         * @param g     the Graphics2D/Graphics object to draw on
         * @param alpha transparency channel, between 0 and 1 inclusive
         */
        private void drawShadowOverlay(Graphics2D g, float alpha) {
            g.setColor(new Color(0f, 0f, 0f, alpha));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        /**
         * Overridden method to cut out need for an alpha channel value
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawShadowOverlay(Graphics2D g) {
            drawShadowOverlay(g, 0.5f);
        }

        /**
         * Draws the player card graphic with name, game piece, and game wedges overlaid
         * <p>
         * https://i.imgur.com/uSZPhrZ.png
         *
         * @param g the Graphics2D/Graphics object to draw on
         */
        private void drawPlayerCards(Graphics2D g) {

            if (game == null) {
                return;
            }

            int x = 20;
            int y = getHeight() / 2 - graphicAssets.scaledCoordinate(144) * players.length / 2;


            for (Player player : players) {

                image = graphicAssets.getImage("empty_Player_Card.png");

                // draw the base blank player card
                g.drawImage(image, x, y, this);

                // set the font and color
                g.setFont(new Font("Calibri", Font.BOLD, 16));
                g.setColor(Color.black);

                // draw the players name
                g.drawString(player.getPlayerName(), x + graphicAssets.scaledCoordinate(152), y + graphicAssets.scaledCoordinate(42));

                // draw the players icon
                switch (player.getGamePiece() % 6) {
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

                player.playerImage = image;

                g.drawImage(image, x + graphicAssets.scaledCoordinate(33), y + graphicAssets.scaledCoordinate(10), null);

                boolean[] wedges = player.getWedges();

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