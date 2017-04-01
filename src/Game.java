import java.io.IOException;
import java.util.*;

/**
 * Created by Colton on 4/1/2017.
 */
public class Game {

    private int numPlayers;
    private Player[] players;
    private Die die;
    private CardDeck cardDeck;


    public Game(Player[] players) throws IOException {

        this.players = players;
        this.numPlayers = players.length;

        this.die = new Die();
        this.cardDeck = new CardDeck();

        setPlayersTurnOrder(players);

    }

    // This for-loop triple threat monstrosity is one way to solve the
    // "who goes first" RNG requirement without worrying about duplicates
    private void setPlayersTurnOrder(Player[] players) {

        ArrayList<Integer> turns = new ArrayList<>(numPlayers);

        for (int i = 0; i < this.numPlayers; i++)
            turns.add(i);

        // it's random?
        Collections.shuffle(turns);

        // Dish out the shuffled turns
        for (int i = 0; i < numPlayers; i++)
            players[i].setTurnOrder(turns.get(i));

        // rearrange the players array to be in turn order instead
        // of creation order
        for (int i = 0; i < numPlayers; i++) {

            if (players[i].getTurnOrder() != i) {

                Player temp = players[i];
                players[i] = players[players[i].getTurnOrder()];
                players[temp.getTurnOrder()] = temp;

            }
        }
    }

    // this is just a test drive. I'm assuming we'll get player array from
    // the GUI class
    /*public static void main(String[] args) throws IOException {

        Player[] testPlayers = new Player[3];

        for (int i = 0; i < testPlayers.length; i++) {

            testPlayers[i] = new Player();
            testPlayers[i].setPlayerName("Shia LaBeouf" + Integer.toString(i));

        }

        Game game = new Game(testPlayers);

        for (Player player : testPlayers)
            System.out.println(player.getPlayerName() + " " + player.getTurnOrder());

    }*/
}
