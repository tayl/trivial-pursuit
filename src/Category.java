import java.awt.*;

/**
 * Created by Colton on 3/30/2017.
 */

// Ideally, the color members for each category should match the RGB of the
// graphics.
//
// Depending on how we do things, we might not even use the color members here,
// but I added them just in case.
public enum  Category {

    // purple
    ARTS (new Color(255,0,255)),

    ENTERTAINMENT (Color.pink),

    EVENTS (Color.yellow),

    PLACES (Color.blue),

    SCIENCE (Color.green),

    SPORTS (Color.orange);


    private final Color color;

    Category(Color color) {
        this.color = color;
    }

    Color color() {
        return this.color;
    }
}
