import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Colton on 4/1/2017.
 * Modified by Daniel on 4/1/2017 (added playTurn, move, questionTime,
 * decideChoise. Edited member variables as needed.
 */

// Colton:
// I created this class to have a space for writing game functionality.
// Any or all of this can be moved or deleted as you see fit.
public class Game implements Runnable {
    // Daniel:
    // the categories for each space on the gameboard
    // 0 = none
    // 1 = Sports
    // 2 = Science
    // 3 = Places
    // 4 = Events
    // 5 = Entertainment
    // 6 = Arts
    private static final int[] SPACE_CATEGORIES = {0, 5, 5, 0, 4, 3, 0, 2, 2, 2, 0,
            5, 3, 0, 6, 6, 6, 0, 2, 1, 0, 3, 3, 3, 0, 6, 4, 0, 1, 1, 1, 0, 3, 5, 0, 4, 4,
            4, 0, 1, 2, 0, 5, 6, 1, 4, 2, 5, 3, 4, 5, 6, 2, 1, 5, 2, 3, 6, 4, 2, 6, 1, 3,
            5, 6, 3, 4, 1, 2, 3, 1, 5, 4
    };

    // the number of players in the current game
    private final int numPlayers;
    // the array of player. Will be in turn order after game constructor
    private final Player[] players;
    // the deck of avaliable cards
    private final CardDeck cardDeck;

    private Card card;
    private Player currentPlayer;
    private boolean lastAnswer;

    private boolean awaitingQuestionPreveiw;
    private boolean awaitingStumpChoice;
    private int stumpChoice;

    private boolean awaitingAnswerChoice;
    private int answerChoice;

    private boolean awaitingRoll;
    private long isRolling;
    private int rollResult;

    private long isWaiting;
    private boolean awaitingEndScreen;

    // Constructor for game
    // takes in the array of players
    public Game(Player[] players) throws IOException {

        this.players = players;
        this.numPlayers = players.length;
        this.awaitingEndScreen = false;

        this.cardDeck = new CardDeck();

        // puts players in the players array based on turn order
        setPlayersTurnOrder(players);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Card getCard() {
        return card;
    }

    public boolean getLastAnswer() {
        return lastAnswer;
    }

    public boolean isAwaitingQuestionPreveiw() {
        return awaitingQuestionPreveiw;
    }

    public void setAwaitingQuestionPreveiw(boolean awaitingQuestionPreveiw) {
        this.awaitingQuestionPreveiw = awaitingQuestionPreveiw;
    }

    public boolean isWaiting() {
        return System.nanoTime() < isWaiting;
    }

    public void setWaiting(int seconds) {
        isWaiting = System.nanoTime() + seconds * 1000000000L;
    }

    public boolean isAwaitingRoll() {
        return awaitingRoll;
    }

    public void setAwaitingRoll(boolean awaiting) {
        awaitingRoll = awaiting;
    }

    public boolean isRolling() {
        return System.nanoTime() - isRolling < 3000000000L;
    }

    public boolean hasRolled() {
        return System.nanoTime() - isRolling > 3000000000L && System.nanoTime() - isRolling < 6000000000L;
    }

    public void setRolling() {
        isRolling = System.nanoTime();
    }

    public int getRollResult() {
        return rollResult;
    }

    public Player[] getPlayers() {
        return players;
    }

    // Colton:
    // This for-loop triple threat monstrosity is one way to solve the
    // "who goes first" RNG requirement without worrying about duplicates
    private void setPlayersTurnOrder2(Player[] players) {

        ArrayList<Integer> turns = new ArrayList<>(numPlayers);

        for (int i = 0; i < this.numPlayers; i++)
            turns.add(i);

        // it's random?
        Collections.shuffle(turns);

        // Dish out the shuffled turn numbers
        for (int i = 0; i < numPlayers; i++)
            players[i].setGamePiece(turns.get(i));

        // rearrange the players array to be in turn order instead
        // of creation order. That way we can easily loop-through
        // turn-based play.
        for (int i = 0; i < numPlayers; i++) {

            if (players[i].getGamePiece() != i) {

                Player temp = players[i];
                players[i] = players[players[i].getGamePiece()];
                players[temp.getGamePiece()] = temp;

            }
        }
    }

    // This achieves the same result. Rearranging of the players array to match turn order didn't actually do that in
    // the previous method, and even if it did, getGamePiece() would return the index, which we have inherently by the
    // nature of an array. Because of this, I left that out.
    private void setPlayersTurnOrder(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            int rand = (int) (Math.random() * players.length);
            Player temp = players[i];
            players[i] = players[rand];
            players[rand] = temp;
        }
    }

    // Daneil:
    // plays the entire ding-dang game, all in one method.
    // returns the winner
    public Player playGame() throws IOException {
        boolean gameOver = false;
        Player winner = null;

        for (Category c : Category.values()) {
            for (int i = 0; i < 20; i++) {
                Card tempCard = cardDeck.drawRandomCard(c);
                int num = 1;
                System.out.println(tempCard.getQuestion());
                System.out.println(num++ + ": " + tempCard.getChoices()[0]);
                System.out.println(num++ + ": " + tempCard.getChoices()[1]);
                System.out.println(num++ + ": " + tempCard.getChoices()[2]);
                System.out.println(num++ + ": " + tempCard.getChoices()[3]);
                System.out.println(tempCard.getCorrectAnsIndex() + ": " + tempCard.getCorrectAns());
                System.out.println();
            }
        }

        // while the game isn't over...
        while (!gameOver) {
            // for each player...
            for (Player p : players) {
                // play our their turn. If they received all wedges...
                try {
                    if (playTurn(p)) {
                        // the game is over
                        gameOver = true;
                        awaitingEndScreen = true;
                        winner = p;

                        while (awaitingEndScreen) {
                            Thread.sleep(50L);
                        }

                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //return the dood who won
        return winner;
    }

    public boolean isAwaitingEndScreen() {
        return awaitingEndScreen;
    }

    public void setAwaitingEndScreen(boolean awaitingEndScreen) {
        this.awaitingEndScreen = awaitingEndScreen;
    }

    // Daniel:
    // runs through a single turn for a single player
    private boolean playTurn(Player player) throws InterruptedException {
        System.out.println(player.getPlayerName() + ", it's your turn!");

        setAwaitingRoll(true);

        while (isAwaitingRoll()) {
            Thread.sleep(50L);
        }

        rollResult = Die.rollThatSucker();

        System.out.println("You rolled a " + rollResult + ".");

        //move the player that number of spaces
        for (int i = 0; i < rollResult; i++) {
            move(player);
        }
        System.out.println("You're now on space " + player.getPosition());

        // then ask the player their question
        Category temp = questionTime(player);

        // award the player a wedge if they deserve it
        if (temp != null) {
            player.setWedge(temp);
        }
        // if the player has all the requisite wedges, they do a final question.
        // Acording to the flow chart on the FRD, they want us to always
        // ask a pointless question before going to the final question, so
        // this does that.
        if (checkWedges(player)) {
            return finalQuestion(player);
        }
        // otherwise, the game continues
        return false;
    }

    // Daniel:
    // Checks if a player has all their wedges in a row
    // returns true if they do
    private boolean checkWedges(Player player) {
        boolean temp = true;
        for (Boolean b : player.getWedges()) {
            if (!b)
                temp = false;
        }
        return temp;
    }

    // Daniel:
    // iterates the player one space foreward. either front end can
    // just update after the entire move is completed, or after each space.
    // Once the player leaves a spoke, they will move around the board in a clockwise fasion.
    private void move(Player player) {
        switch (player.getPosition()) {
            // moves the player from the wheel space. this is random right now, but front end should
            // implement an actionListener to allow the player to choose which spoke they want to go to
            case 0:
                // heres what makes it random
                switch (Die.rollThatSucker()) {
                    case 1:
                        player.setPosition(43);
                        break;
                    case 2:
                        player.setPosition(48);
                        break;
                    case 3:
                        player.setPosition(53);
                        break;
                    case 4:
                        player.setPosition(58);
                        break;
                    case 5:
                        player.setPosition(63);
                        break;
                    case 6:
                        player.setPosition(68);
                        break;
                }
                break;
            // circles around the board
            case 42:
                player.setPosition(1);
                break;
            //decides which space to move to if the player is at the edge of a spoke
            case 47:
            case 52:
            case 57:
            case 62:
            case 67:
            case 72:
                //fancy math magic
                player.setPosition((((player.getPosition() / 6) - 7) * 7) + 1);
                break;
            default:
                // if the player isn't at the center or on the edge of a spoke,
                // just iterate their position
                player.setPosition(player.getPosition() + 1);
                break;
        }
    }

    public boolean isAwaitingStumpChoice() {
        return awaitingStumpChoice;
    }

    public void setStumpChoice(boolean stump) {

        if (stump) {
            stumpChoice = 0;
        } else {
            stumpChoice = 1;
        }

        awaitingStumpChoice = false;
    }

    public boolean isAwaitingAnswerChoice() {
        return awaitingAnswerChoice;
    }

    public void setAnswerChoice(int answer) {
        answerChoice = answer;

        awaitingAnswerChoice = false;
    }

    // Daniel:
    // goes through the process of answering a question from start to finish
    // returns the category of a successfully completed question, null otherwise
    private Category questionTime(Player player) throws InterruptedException {
        Card card;

        //draw a card for the appropriate category. return null if on a white space
        switch (SPACE_CATEGORIES[player.getPosition()]) {
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
        System.out.println(card.getQuestion());
        System.out.println("Do you want to answer the question, or try and stump your opponents? (Enter 1 to answer or 2 to stump)");

        this.card = card;
        currentPlayer = player;

        if (player.isHuman()) {
            awaitingQuestionPreveiw = true;

            while (awaitingQuestionPreveiw) {
                Thread.sleep(50L);
            }
        }

        //if the player chooses to answer
        if (player.isHuman()) {
            awaitingStumpChoice = true;

            while (awaitingStumpChoice) {
                Thread.sleep(50L);
            }
        } else {
            stumpChoice = simpleAI(1);
        }

        if (stumpChoice == 1) {
            System.out.println("Here are your choices. Enter the number for your selection:");
            int i = 1;
            //give them their choices
            for (String s : card.getChoices())
                System.out.println(i++ + ". " + s);

            // get their response. if its correct, move them to the proper space
            // and return the category of the answered question (waaaay at the
            // bottom)

            currentPlayer = player;

            if (player.isHuman()) {
                awaitingAnswerChoice = true;

                while (awaitingAnswerChoice) {
                    Thread.sleep(50L);
                }
            } else {
                answerChoice = simpleAI(2);
            }
            if (answerChoice == card.getCorrectAnsIndex()) {
                System.out.println("Correct answer");

                lastAnswer = true;
                switch (card.getCategory()) {
                    case SPORTS:
                        player.setPosition(29);
                        break;
                    case SCIENCE:
                        player.setPosition(8);
                        break;
                    case PLACES:
                        player.setPosition(22);
                        break;
                    case EVENTS:
                        player.setPosition(36);
                        break;
                    case ENTERTAINMENT:
                        player.setPosition(1);
                        break;
                    case ARTS:
                        player.setPosition(15);
                        break;
                    default:
                        break;
                }
            } else {
                lastAnswer = false;
                System.out.println("Incorrect answer");
                return null;
            }
        }
        // if the player chooses to stump...
        else {
            System.out.println(player.getPlayerName() + " has chosen to stump their opponents! Get ready everyone...");
            //frequency array for stump choices
            int[] temp = new int[card.getChoices().length];

            // for each player...
            for (Player p : players) {
                // that isn't the stumper...
                if (p != player) {
                    int i = 1;
                    // ask them the question...
                    System.out.println(p.getPlayerName() + " enter the number of your selected choice.");
                    System.out.println(card.getQuestion());
                    for (String s : card.getChoices()) {
                        System.out.println(i++ + ". " + s);
                    }

                    currentPlayer = p;

                    // and record their response in the frequency array
                    if (p.isHuman()) {
                        awaitingAnswerChoice = true;

                        while (awaitingAnswerChoice) {
                            Thread.sleep(50L);
                        }
                    } else {
                        answerChoice = simpleAI(2);
                    }

                    temp[answerChoice]++;
                }
            }
            // if the council's answer is correct...
            if (decideChoice(temp) == card.getCorrectAnsIndex()) {
                // player stumped
                System.out.println("Uh-oh, your opponents managed to stump you! They all moved forward one space and get your wedge.");
                lastAnswer = false;
                for (Player p : players) {
                    if (player != p) {
                        move(p);
                        p.setWedge(card.getCategory());
                    }
                }
                // no wedge for you.
                return null;
            }
            // if the council's judgement was mistaken...
            else {
                // move the stumper to the respective home space for the
                // question category
                System.out.println("Hooray! Your opponents couldn't answer the question so the " + card.getCategory() + " wedge goes to you.");
                lastAnswer = true;
                switch (card.getCategory()) {
                    case SPORTS:
                        player.setPosition(29);
                        break;
                    case SCIENCE:
                        player.setPosition(8);
                        break;
                    case PLACES:
                        player.setPosition(22);
                        break;
                    case EVENTS:
                        player.setPosition(36);
                        break;
                    case ENTERTAINMENT:
                        player.setPosition(1);
                        break;
                    case ARTS:
                        player.setPosition(15);
                        break;
                    default:
                        return null;
                }
            }
        }
        return card.getCategory();
    }

    private boolean finalQuestion(Player player) throws InterruptedException {
        Card card;
        System.out.println("Since you've got all your wedges, its time to answer the FINAL QUESTION:");

        switch (SPACE_CATEGORIES[player.getPosition()]) {
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
        System.out.println(card.getQuestion());

        currentPlayer = player;
        this.card = card;
        if (player.isHuman()) {
            awaitingQuestionPreveiw = true;

            while (awaitingQuestionPreveiw) {
                Thread.sleep(50L);
            }
        }
        System.out.println("Please enter the number corresponding to your response.");
        int i = 0;
        for (String s : card.getChoices()) {
            System.out.println(i++ + ". " + s);
        }


        if (player.isHuman()) {
            awaitingAnswerChoice = true;

            while (awaitingAnswerChoice) {
                Thread.sleep(50L);
            }
        } else {
            answerChoice = simpleAI(2);
        }

        if (answerChoice == card.getCorrectAnsIndex()) {
            lastAnswer = true;
            System.out.println("Wowie!!! You won :O");
            return true;
        } else {
            lastAnswer = false;
            System.out.println("Oh gosh, that one was a fail whale. Better luck next time :(");
            return false;
        }
    }

    // Daniel:
    // This function is the council's deliberation.
    // Takes in the frequency array of the stumpees' answers and returns either
    // the most frequent answer, or a random tie breaker of multiple 'most
    // frequent' answers.
    private int decideChoice(int[] choiceFrequency) {
        int highestFrequency = 0;
        int tieBreaker = 0;
        boolean[] choices = new boolean[choiceFrequency.length];

        // run through the selected choices to find the highest observed choice frequency
        for (Integer i : choiceFrequency) {
            if (highestFrequency < i)
                highestFrequency = i;
        }

        // run though it again, flipping the boolean associated with choices with the highest frequency
        int j = 0;
        for (Integer i : choiceFrequency) {
            if (highestFrequency == i) {
                choices[j] = true;
                tieBreaker++;
            }
            j++;
        }

        // j becomes the number of most frequent choices we have to hit
        // until we are on the one selected for the tie breaker.
        j = (int) (Math.random() * tieBreaker) + 1;
        int i = 0;
        for (Boolean b : choices) {
            if (b) {
                if (--j == 0)
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
    private int simpleAI(int choice) {
        switch (choice) {
            //decide direction to move on wheel space
            case 0:
                return Die.rollThatSucker();
            // answer or stump
            case 1:
                return (int) (Math.random() * 2) + 1;
            // answer a question (or provide an answer for stumping)
            case 2:
                return (int) (Math.random() * 4);
            default:
                break;
        }
        System.out.println("Uh oh, something's gone wrong with the AI.");
        return -1;
    }

    @Override
    public void run() {
        try {
            playGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
