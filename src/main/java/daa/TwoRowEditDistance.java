package daa;

public class TwoRowEditDistance implements EditDistance {
    private long cells;
    private long memoryCells;

    // State: previous[j] = D(i-1,j), current[j] = D(i,j) for prefix distances.
    // Transition: equal -> previous[j-1]; else 1 + min(previous[j], current[j-1], previous[j-1]).
    // Base: previous[j] = j initially, and current[0] = i for each row.
    @Override
    public int distance(String a, String b) {
        cells = 0;
        int m = b.length();
        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];
        memoryCells = previous.length + (long) current.length;
        for (int j = 0; j <= m; j++) previous[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= m; j++) {
                cells++;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    current[j] = previous[j - 1];
                } else {
                    current[j] = 1 + Math.min(previous[j],
                            Math.min(current[j - 1], previous[j - 1]));
                }
            }
            int[] temp = previous;
            previous = current;
            current = temp;
        }
        return previous[m];
    }

    @Override
    public long operations() { return cells; }

    @Override
    public long memoryCells() { return memoryCells; }
}
