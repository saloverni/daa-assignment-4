# Experiment Design Card

This card was written before running the experiment. Seed: **42**.

| Field | Plan |
|---|---|
| Questions | Edit distance: how quickly does repeated recursion grow compared with DP, and what memory does two-row DP save? Search: how does repeated-prefix input affect Naive and KMP? |
| Sizes | All edit versions: n = 2, 4, 6, 8, 10, 12, with both strings of length n. Table and two rows also use n = 128, 256, 512, 1024 to obtain useful timings. Search: text n = 4096, 8192, 16384, 32768 and pattern m = n/16. Each range spans at least a factor of four. |
| Edit inputs | Random strings over ABCD show ordinary small-alphabet inputs. Disjoint strings A^n and B^n force all three recursive branches at every non-base state. |
| Search inputs | Random text and pattern over ABCD usually reject a start quickly. Repeated-prefix text A^n and pattern A^(m-1)B force Naive to compare the entire pattern at every start. |
| Metrics | Median time in milliseconds from 5 runs after 3 warm-ups. Edit: recursive calls (including base cases), or interior DP cells. Search: character comparisons, including LPS construction for KMP. Count inside each algorithm. Also record peak DP array slots to show memory use without estimating JVM object overhead. |
| Prediction | Disjoint recursion grows exponentially; DP work grows quadratically. Two rows keep linear DP storage. Random search is close to linear, while the growing repeated-prefix pattern makes Naive quadratic and KMP stays linear. |
| Plots | 1. Disjoint edit work, log y-axis: recursion versus DP. 2. DP array slots: table versus two rows. 3. Search comparisons on both inputs: the repeated-prefix penalty. 4. Median times on larger inputs: how measured time relates to counters. |

Sizes are deliberately capped for recursion. The largest measured n is a chosen
experimental limit, not a claim about the machine's absolute limit. CSV contains
one row per measured call; warm-ups are excluded. Timing includes allocation and
counter updates, but excludes input generation and CSV writing. Exact times are
not reproducible; inputs, results and counters are.
