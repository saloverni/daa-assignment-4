package daa;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class StringSearchTest {
    private StringSearch[] versions() {
        return new StringSearch[]{new NaiveSearch(), new KmpSearch()};
    }

    @Test
    void referenceLpsArrays() {
        KmpSearch kmp = new KmpSearch();
        assertArrayEquals(new int[]{0, 1, 2, 0, 1, 2, 3, 3}, kmp.lps("AAACAAAA"));
        assertArrayEquals(new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, kmp.lps("ABABCABAB"));
        assertArrayEquals(new int[0], kmp.lps(""));
        assertArrayEquals(new int[]{0}, kmp.lps("A"));
    }

    @Test
    void referenceMatchesAndOverlaps() {
        for (StringSearch search : versions()) {
            assertEquals(List.of(10), search.search("ABABDABACDABABCABAB", "ABABCABAB"));
            assertEquals(List.of(0, 1, 2, 3), search.search("AAAAA", "AA"));
            assertEquals(List.of(0, 2, 4), search.search("ABABABA", "ABA"));
            assertEquals(List.of(0), search.search("ABC", "ABC"));
            assertEquals(List.of(0, 2), search.search("ABA", "A"));
        }
    }

    @Test
    void edgeCases() {
        for (StringSearch search : versions()) {
            assertThrows(IllegalArgumentException.class, () -> search.search("abc", ""));
            assertThrows(IllegalArgumentException.class, () -> search.search("", ""));
            assertEquals(List.of(), search.search("A", "AB"));
            assertEquals(List.of(), search.search("", "A"));
            assertEquals(List.of(), search.search("AAA", "B"));
        }
    }

    @Test
    void agreeOn100RandomTextsAndLibraryOracle() {
        Random random = new Random(43);
        for (int sample = 0; sample < 100; sample++) {
            String text = randomString(random, random.nextInt(81));
            String pattern = randomString(random, 1 + random.nextInt(10));
            List<Integer> expected = new ArrayList<>();
            for (int i = text.indexOf(pattern); i >= 0; i = text.indexOf(pattern, i + 1)) {
                expected.add(i);
            }
            for (StringSearch search : versions()) {
                assertEquals(expected, search.search(text, pattern), text + "/" + pattern);
            }
        }
    }

    @Test
    void countersIncludeLpsAndReset() {
        StringSearch naive = new NaiveSearch();
        StringSearch kmp = new KmpSearch();
        naive.search("AAAA", "AAB");
        kmp.search("AAAA", "AAB");
        assertEquals(6, naive.comparisons());
        assertEquals(9, kmp.comparisons());
        for (StringSearch search : new StringSearch[]{naive, kmp}) {
            search.search("A", "A");
            assertEquals(1, search.comparisons());
        }
    }

    private String randomString(Random random, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) s.append("ABC".charAt(random.nextInt(3)));
        return s.toString();
    }
}
