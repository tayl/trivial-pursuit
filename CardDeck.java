import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Colton on 3/30/2017.
 */

//TODO mark Cards as 'used' (or just remove them from their lists in the deck). ()

public class CardDeck {

    // The card deck is a hashmap using Categories as keys
    // and a list of Card objects as values.
    private HashMap<Category, ArrayList<Card>> deck;

    public CardDeck() throws IOException {
        this.deck = new HashMap<>();

        for (Category category : Category.values())
            deck.put(category, new ArrayList<Card>());

        this.setQuestions();
    }

    void setQuestions() {
        try {
            this.readInQuestions(Category.ARTS, "questions/arts-questions.txt");
            this.readInQuestions(Category.ENTERTAINMENT, "questions/ent-questions.txt");
            this.readInQuestions(Category.EVENTS, "questions/events-questions.txt");
            this.readInQuestions(Category.PLACES, "questions/places-questions.txt");
            this.readInQuestions(Category.SCIENCE, "questions/sci-questions.txt");
            this.readInQuestions(Category.SPORTS, "questions/sports-questions.txt");
        } catch (IOException e) {
            System.out.println("Error reading in questions and answers from text files");
        }
    }

    void readInQuestions(Category category, String fileName) throws IOException {

        Scanner in = new Scanner(new File(fileName));

        ArrayList<Card> temp = this.deck.get(category);

        // added to allow setQuestions to be called without disturbing unfinished categories
        if (temp.size() > 0) {
            return;
        }

        while (in.hasNext()) {

            Card card = new Card(category);

            // Each question in the text file is one semicolon-delimited line
            // So we'll read in the line and split it into pieces
            String[] line = in.nextLine().split(";");

            // The first piece is the question itself
            card.setQuestion(line[0]);

            // The next 4 (indices 1 - 4 inclusive) are the multiple choice
            card.setChoices(Arrays.copyOfRange(line, 1, 5));

            // The first index will be the index of the correct answer
            // in the Card's choices array;
            card.setCorrectAnsIndex(0);

            card.shuffleAnswers();

            temp.add(card);

        }

        Collections.shuffle(temp);
    }

    /**
     * @param category The category from which the card is randomly drawn
     * @return The card that was drawn, or null if the category is empty
     */
    public Card drawRandomCard(Category category) {

        ArrayList<Card> temp = this.deck.get(category);

        // once all cards have been randomly drawn, refill that category (and subsequently, any others that are empty)
        if (temp.size() <= 0) {
            setQuestions();
        }

        int randomCard = (int) (Math.random() * temp.size());

        return temp.remove(randomCard);

//        return temp.get((int)(Math.random() * temp.size()));
    }
}
