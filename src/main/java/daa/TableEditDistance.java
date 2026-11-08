package daa;

public class TableEditDistance implements EditDistance {
    private long cells;
    private long memoryCells;

    // State: dp[i][j] is the distance between prefixes of lengths i and j.
    // Transition: equal -> diagonal; otherwise 1 + min(top, left, diagonal).
    // Base: dp[0][j] = j and dp[i][0] = i.
    @Override
    public int distance(String a, String b) {
        cells = 0;
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        memoryCells = (long) (n + 1) * (m + 1);
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                cells++;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                            Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }
        return dp[n][m];
    }

    @Override
    public long operations() { return cells; }

    @Override
    public long memoryCells() { return memoryCells; }
}
