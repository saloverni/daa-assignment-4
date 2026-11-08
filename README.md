# Assignment 4: Dynamic Programming and String Algorithms

Java 21+, Gradle Wrapper, JUnit 5. Python 3 with matplotlib is needed for plots.

```bash
./gradlew build
./gradlew test
./gradlew run
./gradlew plots
```

`run` writes individual measurements to `results/results.csv`. `plots` reads that
file and writes PNG charts to `results/plots/`. Inputs use seed 42; inputs, answers
and operation counters repeat, but measured times vary between runs.

The project contains recursive, table and two-row edit distance, plus Naive and
KMP string search. Counters reset on each call. Search includes overlapping
matches and rejects an empty pattern. Strings are compared as Java `char` values.

The experiment design, measurements and conclusions are in [REPORT.md](REPORT.md).

AI use: ChatGPT assisted with implementation, tests, experiment setup and report.
