import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
/**
 * Created by Colton on 3/30/2017.
 */

//TODO mark Cards as 'used' (or just remove them from their lists in the deck).


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

    void setQuestions() throws IOException {

        this.readInQuestions(Category.ARTS, "questions/arts-questions.txt");
        this.readInQuestions(Category.ENTERTAINMENT, "questions/ent-questions.txt");
        this.readInQuestions(Category.EVENTS, "questions/events-questions.txt");
        this.readInQuestions(Category.PLACES, "questions/places-questions.txt");
        this.readInQuestions(Category.SCIENCE, "questions/sci-questions.txt");
        this.readInQuestions(Category.SPORTS, "questions/sports-questions.txt");

    }

    void readInQuestions(Category category, String fileName) throws IOException {

        Scanner in = new Scanner(new File(fileName));
        
        ArrayList<Card> temp = this.deck.get(category);
        
        while (in.hasNext()) {

            Card card = new Card(category);

            // Each question in the text file is one semicolon-delimited line
            // So we'll read in the line and split it into pieces
            String[] line = in.nextLine().split(";");

            // The first piece is the question itself
            card.setQuestion(line[0]);

            // The next 4 (indices 1 - 4 inclusive) are the multiple choice
            card.setChoices(Arrays.copyOfRange(line, 1, 5));

            // The last piece (index 5) will be the index of the correct answer
            // in the Card's choices array;
            card.setCorrectAnsIndex(Integer.parseInt(line[5]));

            temp.add(card);

        }
        
        Collections.shuffle(temp);
    }

    public Card drawRandomCard(Category category) {

        ArrayList<Card> temp = this.deck.get(category);

        return temp.get((int)(Math.random() * temp.size()));
    }
}
