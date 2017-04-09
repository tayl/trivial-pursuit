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
    int gamePiece;
    //The name of this player
    String playerName;
    //The player's position on the board, as per the space map
    int position = 0;
    //True if this is a human player, false if AI
    boolean human;
    //boolean array for obtained wedges
    boolean[] wedges = new boolean[6];

    Image playerImage;

    //name constructor for Player
    public Player(String playerName) {
        this.playerName = playerName;
        this.position = 0;
    }

    //returns the integer that denotes the player's current space
    public int getPosition() {
        return position;
    }

    //sets the player's current space
    public void setPosition(int position) {
        this.position = position;
    }

    //returns the player's turn priority
    public int getGamePiece() {
        return gamePiece;
    }

    //sets the player's turn priority
    public void setGamePiece(int turnOrder) {
        this.gamePiece = turnOrder;
    }

    //get's the player's name
    public String getPlayerName() {
        return playerName;
    }

    //sets the player's name
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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
