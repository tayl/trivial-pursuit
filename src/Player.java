import java.awt.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Daniel
 */
public class Player {
    //The player's turn priority. first 1 will go, then 2, then 3 and so on
    private int gamePiece;
    //The name of this player
    private String playerName;
    //The player's position on the board, as per the space map
    private int position;
    //The players position as reported to the GUI
    private int drawnPosition;
    //True if this is a human player, false if AI
    private boolean human;
    //boolean array for obtained wedges
    private boolean[] wedges = new boolean[6];

    private Image playerImage;

    //name constructor for Player
    public Player(String playerName) {
        this.playerName = playerName;
    }

    public int getGamePiece() {
        return gamePiece;
    }

    public void setGamePiece(int gamePiece) {
        this.gamePiece = gamePiece;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getDrawnPosition() {
        return drawnPosition;
    }

    public void setDrawnPosition(int drawnPosition) {
        this.drawnPosition = drawnPosition;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isHuman() {
        return human;
    }

    public void setHuman(boolean human) {
        this.human = human;
    }

    public Image getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(Image playerImage) {
        this.playerImage = playerImage;
    }

    //set the wedge that corresponds to the current category
    //make sure to pass an enum Category into this
    public void setWedge(Category category) {
        switch (category) {
            case ARTS:
                wedges[0] = true;
                break;
            case ENTERTAINMENT:
                wedges[1] = true;
                break;
            case EVENTS:
                wedges[2] = true;
                break;
            case PLACES:
                wedges[3] = true;
                break;
            case SCIENCE:
                wedges[4] = true;
                break;
            case SPORTS:
                wedges[5] = true;
                break;
            default:
                break;
        }
    }

    //returns the wedge array
    public boolean[] getWedges() {
        return wedges;
    }
    
    /*public static void main(String [] args)
    {
        Player test = new Player();
        Category testColor = Category.SPORTS;
        test.setWedge(testColor);
        int i = 0;
        for(boolean b: test.wedges)
        {
            
            if(b == true)
                System.out.println(i);
            i++;
        }
        
        test.gamePiece = Die.rollThatSucker();
        System.out.println(test.gamePiece);
    }*/

}
