package daa;

import java.util.Random;

public class InputGenerator {
    private final Random random;

    public InputGenerator(long seed) {
        random = new Random(seed);
    }

    public String randomString(int length) {
        StringBuilder s = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            s.append("ABCD".charAt(random.nextInt(4)));
        }
        return s.toString();
    }

    public String[] editInput(String type, int n) {
        return switch (type) {
            case "random" -> new String[]{randomString(n), randomString(n)};
            case "disjoint" -> new String[]{"A".repeat(n), "B".repeat(n)};
            default -> throw new IllegalArgumentException("Unknown input: " + type);
        };
    }

    public String[] searchInput(String type, int n) {
        int m = n / 16;
        return switch (type) {
            case "random" -> new String[]{randomString(n), randomString(m)};
            case "repeated_prefix" -> new String[]{"A".repeat(n), "A".repeat(m - 1) + "B"};
            default -> throw new IllegalArgumentException("Unknown input: " + type);
        };
    }
}
