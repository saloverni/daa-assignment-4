"""Draw the experiment charts using only the recorded CSV values."""
import csv
from collections import defaultdict
from pathlib import Path
from statistics import median

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "results" / "plots"
OUTPUT.mkdir(parents=True, exist_ok=True)
groups = defaultdict(list)
with (ROOT / "results" / "results.csv").open(newline="") as file:
    for row in csv.DictReader(file):
        groups[(row["algorithm"], row["input"], int(row["n"]))].append(row)

COLORS = {"recursive": "#b84434", "table": "#285ea8", "two_rows": "#21845c",
          "naive": "#b84434", "kmp": "#285ea8"}
LABELS = {"recursive": "Recursive calls", "table": "Table cells",
          "two_rows": "Two-row cells", "naive": "Naive", "kmp": "KMP (including LPS)"}


def series(ax, algorithm, kind, metric, smallest=0, largest=float("inf"), label=None):
    sizes = sorted(n for a, t, n in groups if a == algorithm and t == kind and smallest <= n <= largest)
    values = [median(float(r[metric]) for r in groups[(algorithm, kind, n)]) for n in sizes]
    ax.plot(sizes, values, marker="o", color=COLORS[algorithm],
            linestyle="--" if algorithm == "two_rows" else "-", label=label or LABELS[algorithm])


def save(fig, name):
    for ax in fig.axes:
        ax.grid(alpha=0.25)
        ax.legend(fontsize=9)
    fig.tight_layout()
    fig.savefig(OUTPUT / name, dpi=160)
    plt.close(fig)


fig, ax = plt.subplots(figsize=(7, 3.3))
for algorithm in ["recursive", "table", "two_rows"]:
    series(ax, algorithm, "disjoint", "operations", largest=12)
ax.set(title="Disjoint edit distance: repeated work", xlabel="String length n (characters)",
       ylabel="Work (calls or cells, log scale)", yscale="log")
save(fig, "edit_work.png")

fig, ax = plt.subplots(figsize=(7, 3.3))
for algorithm in ["table", "two_rows"]:
    series(ax, algorithm, "disjoint", "memory_cells", smallest=128,
           label="Full table" if algorithm == "table" else "Two rows")
ax.set(title="Edit distance: allocated DP storage", xlabel="String length n (characters)",
       ylabel="Allocated int slots (log scale)", yscale="log")
save(fig, "edit_memory.png")

fig, axes = plt.subplots(1, 2, figsize=(9, 3.3))
for ax, kind in zip(axes, ["random", "repeated_prefix"]):
    for algorithm in ["naive", "kmp"]:
        series(ax, algorithm, kind, "operations")
    ax.set(title=f"Search: {kind.replace('_', ' ')}", xlabel="Text length n (characters)",
           ylabel="Character comparisons (log scale)", yscale="log")
save(fig, "search_work.png")

fig, axes = plt.subplots(1, 2, figsize=(9, 3.3))
for algorithm in ["table", "two_rows"]:
    series(axes[0], algorithm, "disjoint", "time_ms", smallest=128,
           label="Full table" if algorithm == "table" else "Two rows")
axes[0].set(title="Disjoint DP: median of 5 runs", xlabel="String length n (characters)",
            ylabel="Time (ms, log scale)", yscale="log")
for algorithm in ["naive", "kmp"]:
    series(axes[1], algorithm, "repeated_prefix", "time_ms")
axes[1].set(title="Repeated-prefix search: median of 5", xlabel="Text length n (characters)",
            ylabel="Time (ms, log scale)", yscale="log")
save(fig, "time.png")
print(f"Saved 4 plots to {OUTPUT}")
