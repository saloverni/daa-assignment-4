package daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Experiment {
    private static final int WARMUPS = 3;
    private static final int RUNS = 5;
    private static final long SEED = 42;
    private static volatile int sink;

    public static void main(String[] args) throws IOException {
        InputGenerator generator = new InputGenerator(SEED);
        Files.createDirectories(Path.of("results"));
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(Path.of("results/results.csv")))) {
            csv.println("algorithm,input,n,m,run,time_ms,operations,memory_cells,result,seed");
            for (String type : new String[]{"random", "disjoint"}) {
                for (int n : new int[]{2, 4, 6, 8, 10, 12, 128, 256, 512, 1024}) {
                    String[] pair = generator.editInput(type, n);
                    int expected = new TableEditDistance().distance(pair[0], pair[1]);
                    if (n <= 12) {
                        measureEdit(csv, "recursive", new RecursiveEditDistance(), type, pair, expected);
                    }
                    measureEdit(csv, "table", new TableEditDistance(), type, pair, expected);
                    measureEdit(csv, "two_rows", new TwoRowEditDistance(), type, pair, expected);
                }
            }
            for (String type : new String[]{"random", "repeated_prefix"}) {
                for (int n : new int[]{4096, 8192, 16384, 32768}) {
                    String[] pair = generator.searchInput(type, n);
                    List<Integer> expected = new NaiveSearch().search(pair[0], pair[1]);
                    measureSearch(csv, "naive", new NaiveSearch(), type, pair, expected);
                    measureSearch(csv, "kmp", new KmpSearch(), type, pair, expected);
                }
            }
            if (csv.checkError()) throw new IOException("Failed to write results.csv");
        }
        System.out.println("Saved results/results.csv");
    }

    private static void measureEdit(PrintWriter csv, String name, EditDistance edit,
                                    String type, String[] pair, int expected) {
        for (int i = 0; i < WARMUPS; i++) sink = edit.distance(pair[0], pair[1]);
        double[] times = new double[RUNS];
        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            int answer = edit.distance(pair[0], pair[1]);
            times[run] = (System.nanoTime() - start) / 1_000_000.0;
            sink = answer;
            if (answer != expected) throw new IllegalStateException("Edit results disagree");
            writeRow(csv, name, type, pair, run, times[run], edit.operations(), edit.memoryCells(), answer);
        }
        printMedian(name, type, pair[0].length(), times);
    }

    private static void measureSearch(PrintWriter csv, String name, StringSearch search,
                                      String type, String[] pair, List<Integer> expected) {
        for (int i = 0; i < WARMUPS; i++) sink = search.search(pair[0], pair[1]).size();
        double[] times = new double[RUNS];
        for (int run = 0; run < RUNS; run++) {
            long start = System.nanoTime();
            List<Integer> answer = search.search(pair[0], pair[1]);
            times[run] = (System.nanoTime() - start) / 1_000_000.0;
            sink = answer.size();
            if (!answer.equals(expected)) throw new IllegalStateException("Search results disagree");
            writeRow(csv, name, type, pair, run, times[run], search.comparisons(), 0, answer.size());
        }
        printMedian(name, type, pair[0].length(), times);
    }

    private static void writeRow(PrintWriter csv, String name, String type, String[] pair,
                                 int run, double time, long operations, long memory, int result) {
        csv.printf(Locale.ROOT, "%s,%s,%d,%d,%d,%.6f,%d,%d,%d,%d%n", name, type,
                pair[0].length(), pair[1].length(), run + 1, time, operations, memory, result, SEED);
    }

    private static void printMedian(String name, String type, int n, double[] times) {
        Arrays.sort(times);
        System.out.printf(Locale.ROOT, "%s / %s / n=%d: median %.6f ms%n",
                name, type, n, times[RUNS / 2]);
    }
}
