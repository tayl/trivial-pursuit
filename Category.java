import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Colton on 3/30/2017.
 */

// Ideally, the color members for each category should match the RGB of the
// graphics.
//
// Depending on how we do things, we might not even use the color members here,
// but I added them just in case.
public enum Category {

    // purple
    ARTS(new Color(255, 0, 255)),

    ENTERTAINMENT(Color.pink),

    EVENTS(Color.yellow),

    PLACES(Color.blue),

    SCIENCE(Color.green),

    SPORTS(Color.orange);

    private static final List<Category> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();
    private final Color color;
    Category(Color color) {
        this.color = color;
    }

    public static Category randomCategory() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    Color color() {
        return this.color;
    }
}
