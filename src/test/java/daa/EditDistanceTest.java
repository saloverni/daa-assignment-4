package daa;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class EditDistanceTest {
    private EditDistance[] versions() {
        return new EditDistance[]{new RecursiveEditDistance(),
                new TableEditDistance(), new TwoRowEditDistance()};
    }

    @Test
    void referenceValuesAndEmptyStrings() {
        for (EditDistance edit : versions()) {
            assertEquals(3, edit.distance("kitten", "sitting"));
            assertEquals(2, edit.distance("flaw", "lawn"));
            assertEquals(5, edit.distance("intention", "execution"));
            assertEquals(3, edit.distance("", "abc"));
            assertEquals(3, edit.distance("abc", ""));
            assertEquals(0, edit.distance("abc", "abc"));
            assertEquals(0, edit.distance("", ""));
            assertEquals(1, edit.distance("a", "b"));
        }
    }

    @Test
    void agreeOn100RandomShortPairs() {
        Random random = new Random(42);
        EditDistance[] edits = versions();
        for (int sample = 0; sample < 100; sample++) {
            String a = randomString(random, random.nextInt(7));
            String b = randomString(random, random.nextInt(7));
            int expected = edits[0].distance(a, b);
            assertEquals(expected, edits[1].distance(a, b), a + "/" + b);
            assertEquals(expected, edits[2].distance(a, b), a + "/" + b);
        }
    }

    @Test
    void countersResetAndDescribeWork() {
        EditDistance recursive = new RecursiveEditDistance();
        recursive.distance("a", "b");
        assertEquals(4, recursive.operations());
        recursive.distance("", "a");
        assertEquals(1, recursive.operations());
        for (EditDistance edit : new EditDistance[]{new TableEditDistance(), new TwoRowEditDistance()}) {
            edit.distance("ab", "cde");
            assertEquals(6, edit.operations());
            assertEquals(edit instanceof TableEditDistance ? 12 : 8, edit.memoryCells());
            edit.distance("", "");
            assertEquals(0, edit.operations());
        }
    }

    private String randomString(Random random, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) s.append("ABC".charAt(random.nextInt(3)));
        return s.toString();
    }
}
