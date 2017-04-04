import java.io.IOException;
import java.util.*;

/**
 * Created by Colton on 4/1/2017.
 * Modified by Daniel on 4/1/2017 (added playTurn, move, questionTime,
 * decideChoise. Edited member variables as needed.
 */

// Colton:
// I created this class to have a space for writing game functionality.
// Any or all of this can be moved or deleted as you see fit.
public class Game {
    // Daniel:
    // the categories for each space on the gameboard
    // 0 = none
    // 1 = Sports
    // 2 = Science
    // 3 = Places
    // 4 = Events
    // 5 = Entertainment
    // 6 = Arts
    private static final int [] SPACE_CATEGORIES = {0, 5, 5, 0, 4, 3, 0, 2, 2, 2, 0,
    5, 3, 0, 6, 6, 6, 0, 2, 1, 0, 3, 3, 3, 0, 6, 4, 0, 1, 1, 1, 0, 3, 5, 0, 4, 4,
    4, 0, 1, 2, 0, 5, 6, 1, 4, 2, 5, 3, 4, 5, 6, 2, 1, 5, 2, 3, 6, 4, 2, 6, 1, 3,
    5, 6, 3, 4, 1, 2, 3, 1, 5, 4};
    
    // the number of players in the current game
    private final int numPlayers;
    // the array of player. Will be in turn order after game constructor
    private final Player[] players;
    // the deck of avaliable cards
    private final CardDeck cardDeck;
    
    // Constructor for game
    // takes in the array of players
    public Game(Player[] players) throws IOException {

        this.players = players;
        this.numPlayers = players.length;

        this.cardDeck = new CardDeck();
        
        // puts players in the players array based on turn order
        setPlayersTurnOrder(players);
    }
    
    // Colton:
    // This for-loop triple threat monstrosity is one way to solve the
    // "who goes first" RNG requirement without worrying about duplicates
    private void setPlayersTurnOrder(Player[] players) {

        ArrayList<Integer> turns = new ArrayList<>(numPlayers);

        for (int i = 0; i < this.numPlayers; i++)
            turns.add(i);

        // it's random?
        Collections.shuffle(turns);

        // Dish out the shuffled turn numbers
        for (int i = 0; i < numPlayers; i++)
            players[i].setTurnOrder(turns.get(i));

        // rearrange the players array to be in turn order instead
        // of creation order. That way we can easily loop-through
        // turn-based play.
        for (int i = 0; i < numPlayers; i++) {

            if (players[i].getTurnOrder() != i) {

                Player temp = players[i];
                players[i] = players[players[i].getTurnOrder()];
                players[temp.getTurnOrder()] = temp;

            }
        }
    }
    
    // This achieves the same result. Rearranging of the players array to match turn order didn't actually do that in
    // the previous method, and even if it did, getTurnOrder() would return the index, which we have inherently by the
    // nature of an array. Because of this, I left that out.
    private void setPlayersTurnOrder2(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            int rand = (int) (Math.random() * players.length);
            Player temp = players[i];
            players[i] = players[rand];
            players[rand] = temp;
        }

//        for (int i = 0; i < players.length; i++) {
//            players[i].setTurnOrder(i);
//        }
    }
    
    // Daneil:
    // plays the entire ding-dang game, all in one method.
    // returns the winner
    private Player playGame()
    {
        boolean gameOver = false;
        Player winner = null;
        // while the game isn't over...
        while(!gameOver)
        {
            // for each player...
            for(Player p: players)
            {
                // play our their turn. If they received all wedges...
                if(playTurn(p))
                {
                    // the game is over
                    gameOver = true;
                    winner = p;
                    break;
                }
            }
        }
        //return the dood who won
        return winner;
    }
    
    // Daniel:
    // runs through a single turn for a single player
    private boolean playTurn(Player player)
    {
        System.out.println(player.playerName + ", it's your turn!");
        
        //player rolls a die to decide movement spaces
        int roll = Die.rollThatSucker();
        System.out.println("You rolled a " + roll + ".");
        
        //move the player that number of spaces
        for(int i = 0; i < roll; i++)
            move(player);
        System.out.println("You're now on space " + player.position);
        
        // then ask the player their question
        Category temp = questionTime(player);
        
        // award the player a wedge if they deserve it
        if(temp != null)
        {
            player.setWedge(temp);
        }
        // if the player has all the requisite wedges, they do a final question.
        // Acording to the flow chart on the FRD, they want us to always
        // ask a pointless question before going to the final question, so
        // this does that.
        if(checkWedges(player))
            return finalQuestion(player);
        // otherwise, the game continues
        return false;
    }
    
    // Daniel:
    // Checks if a player has all their wedges in a row
    // returns true if they do
    private boolean checkWedges(Player player)
    {
        boolean temp = true;
        for(Boolean b: player.wedges)
        {
            if(b == false)
                temp = false;
        }
        return temp;
    }
    
    // Daniel:
    // iterates the player one space foreward. either front end can
    // just update after the entire move is completed, or after each space.
    // Once the player leaves a spoke, they will move around the board in a clockwise fasion.
    private void move(Player player)
    {
        switch(player.position)
        {
            // moves the player from the wheel space. this is random right now, but front end should
            // implement an actionListener to allow the player to choose which spoke they want to go to
            case 0:
                // heres what makes it random
                switch(Die.rollThatSucker())
                {
                    case 1: player.position = 43;
                    break;
                    case 2: player.position = 48;
                    break;
                    case 3: player.position = 53;
                    break;
                    case 4: player.position = 58;
                    break;
                    case 5: player.position = 63;
                    break;
                    case 6: player.position = 68;
                    break;
                }   break;
            //decides which space to move to if the player is at the edge of a spoke
            case 47:
            case 52:
            case 57:
            case 62:
            case 67:
            case 72:
                //fancy math magic
                player.position = (((player.position / 6) - 7) * 7) + 1;
                break;
            default:
                // if the player isn't at the center or on the edge of a spoke,
                // just iterate their position
                player.position++;
                break;
        }
    }
    
    // Daniel:
    // goes through the process of answering a question from start to finish
    // returns the category of a successfully completed question, null otherwise
    private Category questionTime(Player player)
    {
        Card card;
        // the user input was just for testing, front end should replace text input
        // with screen prompts and buttons
        Scanner userInput = new Scanner(System.in);
        //draw a card for the appropriate category. return null if on a white space
        switch(SPACE_CATEGORIES[player.position])
        {
            case 1:
                card = cardDeck.drawRandomCard(Category.SPORTS);
                break;
            case 2:
                card = cardDeck.drawRandomCard(Category.SCIENCE);
                break;
            case 3:
                card = cardDeck.drawRandomCard(Category.PLACES);
                break;
            case 4:
                card = cardDeck.drawRandomCard(Category.EVENTS);
                break;
            case 5:
                card = cardDeck.drawRandomCard(Category.ENTERTAINMENT);
                break;
            case 6:
                card = cardDeck.drawRandomCard(Category.ARTS);
                break;
            default:
                return null;
        }
        //give the player the option to answer or stump
        System.out.println("Your question is:");
        System.out.println(card.question);
        System.out.println("Do you want to answer the question, or try and stump your opponents? (Enter 1 to answer or 2 to stump)");
        
        //if the player chooses to answer
        int input;
        if(player.human)
            input = userInput.nextInt();
        else
            input = simpleAI(1);
        if(input == 1)
        {
            System.out.println("Here are your choices. Enter the number for your selection:");
            int i = 0;
            //give them their choices
            for(String s: card.choices)
                System.out.println(i++ + ". " + s);
            
            // get their response. if its correct, move them to the proper space
            // and return the category of the answered question (waaaay at the
            // bottom)
            if(player.human)
                input = userInput.nextInt();
            else
                input = simpleAI(2);
            if(input == card.correctAnsIndex)
            {
                switch(card.category)
                {
                    case SPORTS:
                        player.position = 29;
                        break;
                    case SCIENCE: 
                        player.position = 8;
                        break;
                    case PLACES:
                        player.position = 22;
                        break;
                    case EVENTS:
                        player.position = 36;
                        break;
                    case ENTERTAINMENT:
                        player.position = 1;
                        break;
                    case ARTS:
                        player.position = 15;
                        break;
                    default:
                        break;
                }
            }
            //if they got a fail whale, return null (no wedge given)
            else
                return null;
        }
        // if the player chooses to stump...
        else
        {
            System.out.println(player.playerName + " has chosen to stump their opponents! Get ready everyone...");
            //frequency array for stump choices
            int [] temp = new int[card.choices.length];
            
            // for each player...
            for(Player p: players)
            {
                // that isn't the stumper...
                if(p != player)
                {
                    int i = 0;
                    // ask them the question...
                    System.out.println(p.playerName + " enter the number of your selected choice.");
                    System.out.println(card.question);
                    for(String s: card.choices)
                    {
                        System.out.println(i++ + ". " + s);
                    }
                    // and record their response in the frequency array
                    if(p.human)
                        input = userInput.nextInt();
                    else
                        input = simpleAI(2);
                    temp[input]++;
                }
            }
            // if the council's answer is correct...
            if(decideChoice(temp) == card.correctAnsIndex)
            {
                // STUMPED NERD!!! LELELELL. All other players move foreward a space
                System.out.println("Uh-oh, your oppenents managed to stump you! They all moved forewar one space and get your wedge.");
                for(Player p: players)
                {
                    if(player != p)
                    {
                        move(p);
                        p.setWedge(card.category);
                    }
                }
                // no wedge for you.
                return null;
            }
            // if the council's judgement was mistaken...
            else
            {
                // move the stumper to the respective home space for the
                // question category
                System.out.println("Hooray! Your opponents couldn't answer the question so the " + card.category + "wedge goes to you.");
                switch(card.category)
                {
                    case SPORTS:
                        player.position = 29;
                        break;
                    case SCIENCE: 
                        player.position = 8;
                        break;
                    case PLACES:
                        player.position = 22;
                        break;
                    case EVENTS:
                        player.position = 36;
                        break;
                    case ENTERTAINMENT:
                        player.position = 1;
                        break;
                    case ARTS:
                        player.position = 15;
                        break;
                    default:
                        return null;
                }
            }
        }
        return card.category;
    }
    
    private boolean finalQuestion(Player player)
    {
        Card card;
        Scanner userInput = new Scanner(System.in);
        System.out.println("Since you've got all your wedges, its time to answer the FINAL QUESTION:");
        
        switch(SPACE_CATEGORIES[player.position])
        {
            case 1:
                card = cardDeck.drawRandomCard(Category.SPORTS);
                break;
            case 2:
                card = cardDeck.drawRandomCard(Category.SCIENCE);
                break;
            case 3:
                card = cardDeck.drawRandomCard(Category.PLACES);
                break;
            case 4:
                card = cardDeck.drawRandomCard(Category.EVENTS);
                break;
            case 5:
                card = cardDeck.drawRandomCard(Category.ENTERTAINMENT);
                break;
            case 6:
                card = cardDeck.drawRandomCard(Category.ARTS);
                break;
            default:
                System.out.println("Uh oh, you didn't land on a question space :(. Better luck next time...");
                return false;
        }
        
        System.out.println("Your question is:");
        System.out.println(card.question);
        System.out.println("Please enter the number corresponding to your response.");
        int i = 0;
        for(String s: card.choices)
        {
            System.out.println(i++ + ". " + s);
        }
        
        if(userInput.nextInt() == card.correctAnsIndex)
        {
            System.out.println("Wowie!!! You won :O");
            return true;
        }
        else
        {
            System.out.println("Oh gosh, that one was a fail whale. Better luck next time :(");
            return false;
        }
    }
    
    // Daniel:
    // This function is the council's deliberation.
    // Takes in the frequency array of the stumpees' answers and returns either
    // the most frequent answer, or a random tie breaker of multiple 'most
    // frequent' answers.
    private int decideChoice(int [] choiceFrequency)
    {
        int highestFrequency = 0;
        int tieBreaker = 0;
        boolean [] choices = new boolean[choiceFrequency.length];
        
        // run through the selected choices to find the highest observed choice frequency
        for(Integer i: choiceFrequency)
        {
            if(highestFrequency < i)
                highestFrequency = i;
        }
        
        // run though it again, flipping the boolean associated with choices with the highest frequency
        int j = 0;
        for(Integer i: choiceFrequency)
        {
            if(highestFrequency == i)
            {
                choices[j] = true;
                tieBreaker++;
            }
            j++; 
        }
        
        // j becomes the number of most frequent choices we have to hit
        // until we are on the one selected for the tie breaker.
        j = (int)(Math.random() * tieBreaker) + 1;
        int i = 0;
        for(Boolean b: choices)
        {
            if(b)
            {
                if(--j == 0)
                    return i;
            }
            
            i++;
        }
        // It should never get down to here. If this ever returns -1, theres a problem.
        return -1;
    }
    
    // Daniel:
    // Takes in a single integer representing what choice the AI needs to make
    // and outputs its choice as an iteger.
    private int simpleAI(int choice)
    {
        switch(choice)
        {
            //decide direction to move on wheel space
            case 0:
                return Die.rollThatSucker();
            // answer or stump
            case 1:
                return (int)(Math.random() * 2) + 1;
            // answer a question (or provide an answer for stumping)
            case 2:
                return (int)(Math.random() * 4);
            default:
                break;
        }
        System.out.println("Uh oh, something's gone wrong with the AI.");
        return -1;
    }

    // this is just a test drive. I'm assuming we'll get player array from
    // the GUI class
    public static void main(String[] args) throws IOException {
        Player[] testPlayers = new Player[3];
        
        for (int i = 0; i < testPlayers.length; i++) {
            testPlayers[i] = new Player("Shia LaBeouf" + Integer.toString(i));
        }
        testPlayers[0].human = true;
        testPlayers[0].setWedge(Category.ARTS);
        testPlayers[0].setWedge(Category.SCIENCE);
        testPlayers[0].setWedge(Category.SPORTS);
        testPlayers[0].setWedge(Category.PLACES);
        testPlayers[0].setWedge(Category.EVENTS);
        testPlayers[0].setWedge(Category.ENTERTAINMENT);
        testPlayers[1].human = false;
        testPlayers[2].human = false;
        
        Game game = new Game(testPlayers);
        for (Player player : testPlayers) {
            System.out.println(player.getPlayerName() + " " + player.getTurnOrder());
        }
        
        Player winner = game.playGame();
        
        System.out.println(winner.playerName + " won the game!");
    }
}
