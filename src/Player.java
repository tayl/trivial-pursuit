/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daniel
 */
public class Player {
    //The player's turn priority. first 1 will go, then 2, then 3 and so on
    int turnOrder;
    //The name of this player
    String playerName;
    //The player's position on the board, as per the space map
    int position = 0;

    //returns the integer that denotes the player's current space
    public int getPosition() {
        return position;
    }

    //sets the player's current space
    public void setPosition(int position) {
        this.position = position;
    }
    //boolean array for obtained wedges
    boolean [] wedges = new boolean[6];

    //returns the player's turn priority
    public int getTurnOrder() {
        return turnOrder;
    }

    //sets the player's turn priority
    public void setTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
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
    public void setWedge(Category category)
    {
        switch(category)
        {
            case ARTS:
                wedges[0] = true;
            case ENTERTAINMENT:
                wedges[1] = true;
            case EVENTS:
                wedges[2] = true;
            case PLACES:
                wedges[3] = true;
            case SCIENCE:
                wedges[4] = true;
            case SPORTS:
                wedges[5] = true;
        }
    }

    //returns the wedge array
    public boolean [] getWedges()
    {
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

        test.turnOrder = Die.rollThatSucker();
        System.out.println(test.turnOrder);
    }*/

}