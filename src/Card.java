/**
 * Created by Colton on 3/30/2017.
 */

public class Card {

    Category category;
    String question;
    String[] choices;
    int correctAnsIndex;

    public Card(Category category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public String getCorrectAns() {
        if (choices == null)
            return "no choices available";
        return choices[correctAnsIndex];
    }

    public int getCorrectAnsIndex() {
        return correctAnsIndex;
    }

    public void setCorrectAnsIndex(int correctAnsIndex) {
        this.correctAnsIndex = correctAnsIndex;
    }
}